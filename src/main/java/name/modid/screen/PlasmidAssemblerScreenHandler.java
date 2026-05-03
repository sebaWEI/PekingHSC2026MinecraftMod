package name.modid.screen;

import name.modid.block.entity.PlasmidAssemblerBlockEntity;
import name.modid.component.PartCategory;
import name.modid.item.GeneticPartDefinition;
import name.modid.item.GeneticPartItem;
import name.modid.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 槽位顺序与菜单下标：0–2 顶行元件，3 左中、4 中央质粒、5 右中，6 底中（与发射器 3×3 像素对齐，角上两格不用）。
 */
public class PlasmidAssemblerScreenHandler extends AbstractContainerMenu {
	private static final int CONTAINER_SLOTS = PlasmidAssemblerBlockEntity.INVENTORY_SIZE; // 9 slots (1 core + 8 parts)

	/** Core slot is menu index 4 (added 5th in addSlots). */
	private static final int MENU_INDEX_CORE = 4;

	public PlasmidAssemblerScreenHandler(int syncId, Inventory playerInventory) {
		this(syncId, playerInventory, new SimpleContainer(PlasmidAssemblerBlockEntity.INVENTORY_SIZE));
	}

	public PlasmidAssemblerScreenHandler(int syncId, Inventory playerInventory, Container container) {
		super(ModScreenHandlers.PLASMID_ASSEMBLER, syncId);
		addSlots(container);

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
		}
	}

	private void addSlots(Container container) {
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_PROMOTER, 62, 17));
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_UTR5, 80, 17));
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_CDS, 98, 17));
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_UTR3, 62, 35));
		this.addSlot(new Slot(container, PlasmidAssemblerBlockEntity.SLOT_CORE, 80, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ModItems.BLANK_PLASMID) || stack.is(ModItems.ENGINEERED_PLASMID);
			}
			@Override
			public int getMaxStackSize() { return 1; }
		});
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_ENHANCER, 98, 35));
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_SINEB2, 80, 53));
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_EXTRA1, 62, 53));
		this.addSlot(partSlot(container, PlasmidAssemblerBlockEntity.SLOT_EXTRA2, 98, 53));
	}

	private static Slot partSlot(Container container, int slot, int x, int y) {
		return new Slot(container, slot, x, y) {
			@Override
			public int getMaxStackSize() { return 1; }

			@Override
			public boolean mayPlace(ItemStack stack) {
				if (stack.isEmpty()) return true;
				// Require a plasmid in the core slot before placing any parts
				if (container.getItem(PlasmidAssemblerBlockEntity.SLOT_CORE).isEmpty()) return false;
				if (!(stack.getItem() instanceof GeneticPartItem newPart)) return false;

				// Block duplicate category across all part slots
				var newCat = newPart.getDefinition().category();
				for (int s = PlasmidAssemblerBlockEntity.SLOT_FIRST_PART; s <= PlasmidAssemblerBlockEntity.SLOT_LAST_PART; s++) {
					var existing = container.getItem(s);
					if (existing.getItem() instanceof GeneticPartItem existingPart) {
						if (existingPart.getDefinition().category() == newCat) {
							return false;
						}
					}
				}
				return true;
			}
		};
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack stack = slot.getItem();
		ItemStack copy = stack.copy();

		if (index < CONTAINER_SLOTS) {
			if (!this.moveItemStackTo(stack, CONTAINER_SLOTS, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else {
			if (stack.is(ModItems.BLANK_PLASMID) || stack.is(ModItems.ENGINEERED_PLASMID)) {
				if (!this.moveItemStackTo(stack, MENU_INDEX_CORE, MENU_INDEX_CORE + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (stack.getItem() instanceof GeneticPartItem) {
				boolean moved = this.moveItemStackTo(stack, 0, MENU_INDEX_CORE, false);
				if (!moved) {
					moved = this.moveItemStackTo(stack, MENU_INDEX_CORE + 1, CONTAINER_SLOTS, false);
				}
				if (!moved) return ItemStack.EMPTY;
			} else {
				return ItemStack.EMPTY;
			}
		}

		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return copy;
	}
}
