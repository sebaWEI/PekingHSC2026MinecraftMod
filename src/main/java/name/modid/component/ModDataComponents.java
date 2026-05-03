package name.modid.component;

import name.modid.IgemMod;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModDataComponents {
	private ModDataComponents() {
	}

	public static final DataComponentType<GeneticDesign> GENETIC_DESIGN = Registry.register(
		BuiltInRegistries.DATA_COMPONENT_TYPE,
		Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "genetic_design"),
		DataComponentType.<GeneticDesign>builder().persistent(GeneticDesign.CODEC).build()
	);

	/** Marks an item as already bio-enhanced to prevent stacking. */
	public static final DataComponentType<BioEnhancements> BIO_ENHANCEMENTS = Registry.register(
		BuiltInRegistries.DATA_COMPONENT_TYPE,
		Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "bio_enhancements"),
		DataComponentType.<BioEnhancements>builder().persistent(BioEnhancements.CODEC).build()
	);

	/** Hidden seed that locks a tissue's discovery result. Packed: rarityOrdinal * 10 + categoryOrdinal. */
	public static final DataComponentType<Integer> TISSUE_IDENTITY = Registry.register(
		BuiltInRegistries.DATA_COMPONENT_TYPE,
		Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "tissue_identity"),
		DataComponentType.<Integer>builder().persistent(net.minecraft.util.ExtraCodecs.NON_NEGATIVE_INT).build()
	);

	public static void initialize() {
		IgemMod.LOGGER.info("Mod data components ready.");
	}
}

