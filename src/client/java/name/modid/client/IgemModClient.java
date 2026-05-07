package name.modid.client;

import name.modid.client.screen.IncubatorScreen;
import name.modid.client.screen.PlasmidAssemblerScreen;
import name.modid.item.GuideBookItem;
import name.modid.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class IgemModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModScreenHandlers.INCUBATOR, IncubatorScreen::new);
		MenuScreens.register(ModScreenHandlers.PLASMID_ASSEMBLER, PlasmidAssemblerScreen::new);

		// Wire up guide book screen opener (client-only code)
		GuideBookItem.setOpener(new GuideBookClientOpener());
	}
}