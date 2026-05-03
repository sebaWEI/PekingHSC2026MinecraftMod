package name.modid.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * PETase protein — a recycling tool that breaks down blocks into raw materials.
 *
 * In real science, PETase is a bacterial enzyme discovered in 2016 at a Japanese
 * recycling plant (Ideonella sakaiensis). It can digest PET plastic — one of
 * synthetic biology's most promising solutions to the global plastic pollution crisis.
 *
 * In-game: right-click on certain blocks to recycle them into raw materials.
 * Yield scales with protein tier.
 */
public class PetaseRecyclerItem extends Item {

	public PetaseRecyclerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		ItemStack petase = context.getItemInHand();
		Direction face = context.getClickedFace();

		if (level.isClientSide()) return InteractionResult.SUCCESS;

		int tier = getTier(petase);
		ItemStack recycleResult = getRecycleResult(state, tier);

		if (recycleResult == null || recycleResult.isEmpty()) {
			context.getPlayer().sendSystemMessage(
				Component.translatable("tooltip.synbio.petase.cannot_recycle"));
			return InteractionResult.FAIL;
		}

		// Remove the block
		level.removeBlock(pos, false);

		// Drop the recycled materials at the block position
		ItemEntity itemEntity = new ItemEntity(
			level,
			pos.getX() + 0.5 + face.getStepX() * 0.3,
			pos.getY() + 0.5 + face.getStepY() * 0.3,
			pos.getZ() + 0.5 + face.getStepZ() * 0.3,
			recycleResult
		);
		level.addFreshEntity(itemEntity);

		level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.6f, 1.2f);

		// Consume one PETase protein
		petase.shrink(1);

		context.getPlayer().sendSystemMessage(
			Component.translatable("tooltip.synbio.petase.recycled"));

		return InteractionResult.SUCCESS;
	}

	/**
	 * Map block → recycled output, scaled by tier.
	 */
	private static ItemStack getRecycleResult(BlockState state, int tier) {
		Block block = state.getBlock();

		// Glass → Sand (metaphor for depolymerization)
		if (block == Blocks.GLASS || block == Blocks.GLASS_PANE ||
			block == Blocks.WHITE_STAINED_GLASS || block == Blocks.WHITE_STAINED_GLASS_PANE ||
			block == Blocks.ORANGE_STAINED_GLASS || block == Blocks.ORANGE_STAINED_GLASS_PANE ||
			block == Blocks.MAGENTA_STAINED_GLASS || block == Blocks.MAGENTA_STAINED_GLASS_PANE ||
			block == Blocks.LIGHT_BLUE_STAINED_GLASS || block == Blocks.LIGHT_BLUE_STAINED_GLASS_PANE ||
			block == Blocks.YELLOW_STAINED_GLASS || block == Blocks.YELLOW_STAINED_GLASS_PANE ||
			block == Blocks.LIME_STAINED_GLASS || block == Blocks.LIME_STAINED_GLASS_PANE ||
			block == Blocks.PINK_STAINED_GLASS || block == Blocks.PINK_STAINED_GLASS_PANE ||
			block == Blocks.GRAY_STAINED_GLASS || block == Blocks.GRAY_STAINED_GLASS_PANE ||
			block == Blocks.LIGHT_GRAY_STAINED_GLASS || block == Blocks.LIGHT_GRAY_STAINED_GLASS_PANE ||
			block == Blocks.CYAN_STAINED_GLASS || block == Blocks.CYAN_STAINED_GLASS_PANE ||
			block == Blocks.PURPLE_STAINED_GLASS || block == Blocks.PURPLE_STAINED_GLASS_PANE ||
			block == Blocks.BLUE_STAINED_GLASS || block == Blocks.BLUE_STAINED_GLASS_PANE ||
			block == Blocks.BROWN_STAINED_GLASS || block == Blocks.BROWN_STAINED_GLASS_PANE ||
			block == Blocks.GREEN_STAINED_GLASS || block == Blocks.GREEN_STAINED_GLASS_PANE ||
			block == Blocks.RED_STAINED_GLASS || block == Blocks.RED_STAINED_GLASS_PANE ||
			block == Blocks.BLACK_STAINED_GLASS || block == Blocks.BLACK_STAINED_GLASS_PANE) {
			int count = tier >= 3 ? 1 : (tier >= 2 ? 1 : (Math.random() < 0.5 ? 1 : 0));
			if (count <= 0) return null;
			return new ItemStack(Items.SAND, count);
		}

		// Wool → String
		if (block == Blocks.WHITE_WOOL || block == Blocks.ORANGE_WOOL ||
			block == Blocks.MAGENTA_WOOL || block == Blocks.LIGHT_BLUE_WOOL ||
			block == Blocks.YELLOW_WOOL || block == Blocks.LIME_WOOL ||
			block == Blocks.PINK_WOOL || block == Blocks.GRAY_WOOL ||
			block == Blocks.LIGHT_GRAY_WOOL || block == Blocks.CYAN_WOOL ||
			block == Blocks.PURPLE_WOOL || block == Blocks.BLUE_WOOL ||
			block == Blocks.BROWN_WOOL || block == Blocks.GREEN_WOOL ||
			block == Blocks.RED_WOOL || block == Blocks.BLACK_WOOL) {
			int count = tier >= 4 ? 4 : (tier >= 3 ? 3 : (tier >= 2 ? 2 : 1));
			return new ItemStack(Items.STRING, count);
		}

		// Terracotta → Clay (metaphor: baked clay → raw clay)
		if (block == Blocks.TERRACOTTA || block == Blocks.WHITE_TERRACOTTA ||
			block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA ||
			block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA ||
			block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA ||
			block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA ||
			block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA ||
			block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA ||
			block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA ||
			block == Blocks.BLACK_TERRACOTTA) {
			int count = tier >= 3 ? 1 : (Math.random() < 0.5 ? 1 : 0);
			if (count <= 0) return null;
			return new ItemStack(Items.CLAY_BALL, count);
		}

		return null;
	}

	private static int getTier(ItemStack stack) {
		if (stack.is(ModItems.PROTEIN_PETASE_TIER4)) return 4;
		if (stack.is(ModItems.PROTEIN_PETASE_TIER3)) return 3;
		if (stack.is(ModItems.PROTEIN_PETASE_TIER2)) return 2;
		return 1;
	}
}
