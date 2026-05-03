package name.modid.block;

import com.mojang.serialization.MapCodec;
import name.modid.block.entity.PlasmidAssemblerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PlasmidAssemblerBlock extends Block implements EntityBlock {
	public static final MapCodec<PlasmidAssemblerBlock> CODEC = simpleCodec(PlasmidAssemblerBlock::new);

	public PlasmidAssemblerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PlasmidAssemblerBlockEntity(pos, state);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) return InteractionResult.SUCCESS;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof PlasmidAssemblerBlockEntity assembler) {
			player.openMenu(assembler);
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return useWithoutItem(state, level, pos, player, hitResult);
	}
}
