package name.modid.block;

import name.modid.IgemMod;
import name.modid.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class ModBlocks {
	private ModBlocks() {
	}

	public static final Block INCUBATOR = register(
		"incubator",
		IncubatorBlock::new,
		BlockBehaviour.Properties.of().strength(3.0F).lightLevel(state -> state.getValue(IncubatorBlock.WORKING) ? 13 : 0)
	);

	public static final Block PLASMID_ASSEMBLER = register(
		"plasmid_assembler",
		PlasmidAssemblerBlock::new,
		BlockBehaviour.Properties.of().strength(2.5F)
	);

	private static <T extends Block> T register(String path, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, path));
		// 26.x / 1.21.2+：Block 也需要在 Properties 上绑定 id，否则运行时报 “Block id not set”。
		T block = factory.apply(properties.setId(key));
		Registry.register(BuiltInRegistries.BLOCK, key, block);

		// Block item
		ModItems.registerForBlockItem(path, props -> new BlockItem(block, props), new Item.Properties());
		return block;
	}

	public static void initialize() {
		IgemMod.LOGGER.info("Mod blocks ready.");
	}
}

