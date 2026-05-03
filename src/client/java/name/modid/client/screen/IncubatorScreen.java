package name.modid.client.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import name.modid.screen.IncubatorScreenHandler;

public class IncubatorScreen extends AbstractContainerScreen<IncubatorScreenHandler> {
	private static final Identifier BREWING_STAND_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/brewing_stand.png");

	public IncubatorScreen(IncubatorScreenHandler handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		graphics.blit(BREWING_STAND_TEXTURE, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0f, 0f, 1f, 1f);
	}
}

