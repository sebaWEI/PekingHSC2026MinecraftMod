package name.modid.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import name.modid.client.screen.IncubatorScreen;
import name.modid.client.screen.PlasmidAssemblerScreen;
import name.modid.screen.ModScreenHandlers;

public class IgemModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModScreenHandlers.INCUBATOR, IncubatorScreen::new);
		MenuScreens.register(ModScreenHandlers.PLASMID_ASSEMBLER, PlasmidAssemblerScreen::new);
	}
}