package name.modid.screen;

import name.modid.IgemMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModScreenHandlers {
	private ModScreenHandlers() {
	}

	public static final MenuType<IncubatorScreenHandler> INCUBATOR = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "incubator"),
		new MenuType<>(IncubatorScreenHandler::new, FeatureFlags.VANILLA_SET)
	);

	public static final MenuType<PlasmidAssemblerScreenHandler> PLASMID_ASSEMBLER = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "plasmid_assembler"),
		new MenuType<>(PlasmidAssemblerScreenHandler::new, FeatureFlags.VANILLA_SET)
	);

	public static void initialize() {
		IgemMod.LOGGER.info("Mod screen handlers ready.");
	}
}

