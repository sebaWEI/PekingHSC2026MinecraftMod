package name.modid.screen;

import name.modid.block.entity.IncubatorBlockEntity;
import name.modid.item.ModItems;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class IncubatorScreenHandler extends AbstractContainerMenu {
	private final Container container;

	/**
	 * Client-side constructor (no block pos in vanilla menu type).
	 * We use a dummy container; server will sync slot contents.
	 */
	public IncubatorScreenHandler(int syncId, Inventory playerInventory) {
		this(syncId, playerInventory, new SimpleContainer(5));
	}

	public IncubatorScreenHandler(int syncId, Inventory playerInventory, Container container) {
		super(ModScreenHandlers.INCUBATOR, syncId);
		this.container = container;

		// Slots aligned to brewing stand background.
		// Broth (slot 0): X=17, Y=17
		this.addSlot(new Slot(container, IncubatorBlockEntity.SLOT_BROTH, 17, 17) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return ModItems.getBrothTier(stack) != null;
			}
		});

		// Outputs (slots 1-3): X=56,51 ; 79,58 ; 102,51
		this.addSlot(new Slot(container, IncubatorBlockEntity.SLOT_OUTPUT_1, 56, 51) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		});
		this.addSlot(new Slot(container, IncubatorBlockEntity.SLOT_OUTPUT_2, 79, 58) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		});
		this.addSlot(new Slot(container, IncubatorBlockEntity.SLOT_OUTPUT_3, 102, 51) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		});

		// Bacteria input (slot 4): X=79, Y=17
		this.addSlot(new Slot(container, IncubatorBlockEntity.SLOT_BACTERIA, 79, 17) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ModItems.E_COLI_ENGINEERED);
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});

		// Player inventory
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		// Hotbar
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack original = slot.getItem();
		newStack = original.copy();

		int containerSlots = 5;
		if (index < containerSlots) {
			// Move from incubator -> player inventory
			if (!this.moveItemStackTo(original, containerSlots, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else {
			// Move from player -> incubator inputs
			if (ModItems.getBrothTier(original) != null) {
				if (!this.moveItemStackTo(original, IncubatorBlockEntity.SLOT_BROTH, IncubatorBlockEntity.SLOT_BROTH + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (original.is(ModItems.E_COLI_ENGINEERED)) {
				if (!this.moveItemStackTo(original, IncubatorBlockEntity.SLOT_BACTERIA, IncubatorBlockEntity.SLOT_BACTERIA + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				return ItemStack.EMPTY;
			}
		}

		if (original.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return newStack;
	}

	public Container getContainer() {
		return container;
	}
}

