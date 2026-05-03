package name.modid.block.entity;

import name.modid.block.IncubatorBlock;
import name.modid.component.GeneticDesign;
import name.modid.component.ModDataComponents;
import name.modid.component.ProteinTier;
import name.modid.item.ModItems;
import name.modid.item.BrothTier;
import name.modid.inventory.ImplementedInventory;
import name.modid.recipe.ExpressionRecipe;
import name.modid.screen.IncubatorScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class IncubatorBlockEntity extends BlockEntity implements ImplementedInventory, MenuProvider {
	public static final int SLOT_BROTH = 0;
	public static final int SLOT_OUTPUT_1 = 1;
	public static final int SLOT_OUTPUT_2 = 2;
	public static final int SLOT_OUTPUT_3 = 3;
	public static final int SLOT_BACTERIA = 4;

	private static final int PROCESS_TICKS = 200;

	private final NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
	private int progress = 0;

	public IncubatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.INCUBATOR, pos, state);
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.synbio.incubator");
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
		return new IncubatorScreenHandler(syncId, playerInventory, this);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, IncubatorBlockEntity be) {
		if (level.isClientSide()) return;

		ItemStack bacteria = be.getItem(SLOT_BACTERIA);
		ItemStack broth = be.getItem(SLOT_BROTH);

		boolean canWork = isValidInput(bacteria, broth) && canAcceptOutput(be);

		if (!canWork) {
			be.progress = 0;
			if (state.getValue(IncubatorBlock.WORKING)) {
				level.setBlock(pos, state.setValue(IncubatorBlock.WORKING, false), 3);
			}
			return;
		}

		if (!state.getValue(IncubatorBlock.WORKING)) {
			level.setBlock(pos, state.setValue(IncubatorBlock.WORKING, true), 3);
		}

		be.progress++;
		if (be.progress < PROCESS_TICKS) return;
		be.progress = 0;

		// Re-check just before finishing (avoid consuming if output got filled).
		if (!isValidInput(bacteria, broth) || !canAcceptOutput(be)) return;

		GeneticDesign design = bacteria.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		ItemStack result = ExpressionRecipe.getExpressionResult(design);
		if (result.isEmpty()) return;

		// Consume 1 broth per completed cycle.
		broth.shrink(1);
		if (broth.isEmpty()) be.setItem(SLOT_BROTH, ItemStack.EMPTY);

		insertIntoOutputs(be, result);
		be.setChanged();
	}

	private static boolean isValidInput(ItemStack bacteria, ItemStack broth) {
		if (bacteria.isEmpty() || broth.isEmpty()) return false;
		if (!bacteria.is(ModItems.E_COLI_ENGINEERED)) return false;
		BrothTier brothTier = ModItems.getBrothTier(broth);
		if (brothTier == null) return false;
		GeneticDesign design = bacteria.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		if (!design.hasCds()) return false;

		ProteinTier proteinTier = design.getProteinTier();
		return brothTier.allows(proteinTier);
	}

	/**
	 * Output Slot Rule A:
	 * Fill slot1 -> slot2 -> slot3. If none can accept, do not start/pause progress and do not consume broth.
	 */
	private static boolean canAcceptOutput(IncubatorBlockEntity be) {
		GeneticDesign design = be.getItem(SLOT_BACTERIA).getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		ItemStack result = ExpressionRecipe.getExpressionResult(design);
		if (result.isEmpty()) return false;

		return canMergeInto(be.getItem(SLOT_OUTPUT_1), result)
			|| canMergeInto(be.getItem(SLOT_OUTPUT_2), result)
			|| canMergeInto(be.getItem(SLOT_OUTPUT_3), result);
	}

	private static boolean canMergeInto(ItemStack target, ItemStack stack) {
		if (target.isEmpty()) return true;
		if (!ItemStack.isSameItemSameComponents(target, stack)) return false;
		return target.getCount() < target.getMaxStackSize();
	}

	private static void insertIntoOutputs(IncubatorBlockEntity be, ItemStack stack) {
		if (tryMergeInto(be, SLOT_OUTPUT_1, stack)) return;
		if (tryMergeInto(be, SLOT_OUTPUT_2, stack)) return;
		tryMergeInto(be, SLOT_OUTPUT_3, stack);
	}

	private static boolean tryMergeInto(IncubatorBlockEntity be, int slot, ItemStack stack) {
		ItemStack target = be.getItem(slot);
		if (target.isEmpty()) {
			be.setItem(slot, stack.copyWithCount(1));
			return true;
		}
		if (!ItemStack.isSameItemSameComponents(target, stack)) return false;
		if (target.getCount() >= target.getMaxStackSize()) return false;
		target.grow(1);
		be.setItem(slot, target);
		return true;
	}

}

