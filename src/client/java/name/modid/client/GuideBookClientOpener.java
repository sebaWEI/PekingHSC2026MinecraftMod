package name.modid.client;

import name.modid.client.screen.GuideBookScreen;
import name.modid.item.GuideBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Client-side implementation of the guide book screen opener.
 * Registered via {@link GuideBookItem#setOpener(GuideBookItem.GuideBookScreenOpener)} during client init.
 */
public class GuideBookClientOpener implements GuideBookItem.GuideBookScreenOpener {
    @Override
    public void open(List<Component> pages) {
        Minecraft.getInstance().setScreen(new GuideBookScreen(pages));
    }
}
