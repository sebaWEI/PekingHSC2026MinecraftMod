package name.modid.block.entity;

import name.modid.component.GeneticDesign;
import name.modid.component.InsertedRegulatoryPart;
import name.modid.component.ModDataComponents;
import name.modid.inventory.ImplementedInventory;
import name.modid.item.GeneticPartItem;
import name.modid.item.ModItems;
import name.modid.screen.PlasmidAssemblerScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Slot 0：质粒；1–8：零件（任意类型，同类不重复）。9 格对应 dispenser 3×3 全网格。
 */
public class PlasmidAssemblerBlockEntity extends BlockEntity implements ImplementedInventory, MenuProvider {
	public static final int SLOT_CORE = 0;
	public static final int SLOT_PROMOTER = 1;
	public static final int SLOT_UTR5 = 2;
	public static final int SLOT_CDS = 3;
	public static final int SLOT_UTR3 = 4;
	public static final int SLOT_ENHANCER = 5;
	public static final int SLOT_SINEB2 = 6;
	public static final int SLOT_EXTRA1 = 7;
	public static final int SLOT_EXTRA2 = 8;

	public static final int SLOT_FIRST_PART = SLOT_PROMOTER;
	public static final int SLOT_LAST_PART = SLOT_EXTRA2;

	public static final int INVENTORY_SIZE = SLOT_LAST_PART + 1;

	private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
	private boolean unpacking;

	public PlasmidAssemblerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PLASMID_ASSEMBLER, pos, state);
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.synbio.plasmid_assembler");
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
		return new PlasmidAssemblerScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		ImplementedInventory.super.setItem(slot, stack);
		if (this.level != null && !this.level.isClientSide()) {
			onInventorySlotChanged(slot);
		}
	}

	private void onInventorySlotChanged(int slot) {
		if (unpacking) return;

		if (slot == SLOT_CORE) {
			ItemStack core = items.get(SLOT_CORE);
			if (core.isEmpty()) {
				clearPartSlotsWithoutDrops();
				return;
			}
			if (isPlasmid(core)) {
				unpackPlasmidIntoPartSlots();
			}
			return;
		}

		if (isPlasmid(items.get(SLOT_CORE))) {
			applyDesignFromPartSlots();
		}
	}

	private static boolean isPlasmid(ItemStack stack) {
		return !stack.isEmpty() && (stack.is(ModItems.BLANK_PLASMID) || stack.is(ModItems.ENGINEERED_PLASMID));
	}

	private void clearPartSlotsWithoutDrops() {
		unpacking = true;
		try {
			for (int s = SLOT_FIRST_PART; s <= SLOT_LAST_PART; s++) {
				items.set(s, ItemStack.EMPTY);
			}
		} finally {
			unpacking = false;
		}
	}

	private void applyDesignFromPartSlots() {
		ItemStack core = items.get(SLOT_CORE);
		if (core.isEmpty() || !isPlasmid(core)) return;

		GeneticDesign built = GeneticDesign.EMPTY;
		for (int slot = SLOT_FIRST_PART; slot <= SLOT_LAST_PART; slot++) {
			ItemStack st = items.get(slot);
			if (st.isEmpty()) continue;
			if (!(st.getItem() instanceof GeneticPartItem partItem)) continue;
			built = built.withInsertedPart(partItem.getDefinition());
		}

		if (!built.hasCds() && built.regulatoryParts().isEmpty()) {
			ItemStack blank = new ItemStack(ModItems.BLANK_PLASMID, 1);
			unpacking = true;
			try {
				items.set(SLOT_CORE, blank);
			} finally {
				unpacking = false;
			}
			return;
		}

		ItemStack engineered = new ItemStack(ModItems.ENGINEERED_PLASMID, 1);
		engineered.set(ModDataComponents.GENETIC_DESIGN, built);
		unpacking = true;
		try {
			items.set(SLOT_CORE, engineered);
		} finally {
			unpacking = false;
		}
	}

	private void unpackPlasmidIntoPartSlots() {
		unpacking = true;
		try {
			for (int s = SLOT_FIRST_PART; s <= SLOT_LAST_PART; s++) {
				items.set(s, ItemStack.EMPTY);
			}

			ItemStack core = items.get(SLOT_CORE);
			if (core.isEmpty()) return;

			GeneticDesign d = core.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);

			if (d.hasCds()) {
				Item cdsItem = ModItems.itemForCdsId(d.cdsId());
				if (cdsItem != null) {
					items.set(SLOT_CDS, new ItemStack(cdsItem, 1));
				}
			}

			for (InsertedRegulatoryPart p : d.regulatoryParts()) {
				Item partItem = ModItems.getRegulatoryPartItem(p.category(), p.rarity());
				if (partItem == null) continue;
				switch (p.category()) {
					case PROMOTER -> items.set(SLOT_PROMOTER, new ItemStack(partItem, 1));
					case UTR5 -> items.set(SLOT_UTR5, new ItemStack(partItem, 1));
					case UTR3 -> items.set(SLOT_UTR3, new ItemStack(partItem, 1));
					case ENHANCER -> items.set(SLOT_ENHANCER, new ItemStack(partItem, 1));
					case SINEB2 -> items.set(SLOT_SINEB2, new ItemStack(partItem, 1));
				}
			}
		} finally {
			unpacking = false;
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items, false);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items.clear();
		ContainerHelper.loadAllItems(input, items);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveWithoutMetadata(registries);
	}
}
