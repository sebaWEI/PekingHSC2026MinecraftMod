package name.modid.block.entity;

import name.modid.IgemMod;
import name.modid.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	private ModBlockEntities() {
	}

	public static final BlockEntityType<IncubatorBlockEntity> INCUBATOR = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "incubator"),
		FabricBlockEntityTypeBuilder.create(IncubatorBlockEntity::new, ModBlocks.INCUBATOR).build()
	);

	public static final BlockEntityType<PlasmidAssemblerBlockEntity> PLASMID_ASSEMBLER = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "plasmid_assembler"),
		FabricBlockEntityTypeBuilder.create(PlasmidAssemblerBlockEntity::new, ModBlocks.PLASMID_ASSEMBLER).build()
	);

	public static void initialize() {
		IgemMod.LOGGER.info("Mod block entities ready.");
	}
}

