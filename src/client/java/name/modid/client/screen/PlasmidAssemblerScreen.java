package name.modid.client.screen;

import name.modid.screen.PlasmidAssemblerScreenHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PlasmidAssemblerScreen extends AbstractContainerScreen<PlasmidAssemblerScreenHandler> {
	private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/dispenser.png");

	public PlasmidAssemblerScreen(PlasmidAssemblerScreenHandler handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractBackground(graphics, mouseX, mouseY, delta);

		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;

		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 0f,
			this.imageWidth, this.imageHeight, 256, 256);
	}
}
