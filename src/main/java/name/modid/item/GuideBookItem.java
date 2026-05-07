package name.modid.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Right-click to open the SynBio Crafter guide book.
 * Content is loaded from translatable lang keys for bilingual support.
 * The screen opener must be set during client initialization via {@link #setOpener}.
 */
public class GuideBookItem extends Item {
    private static final int PAGE_COUNT = 16;
    private static GuideBookScreenOpener opener = pages -> {};

    public GuideBookItem(Properties properties) {
        super(properties);
    }

    /** Called from client initializer to wire up the screen opening. */
    public static void setOpener(GuideBookScreenOpener o) {
        opener = o;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            List<Component> pages = new ArrayList<>();
            for (int i = 0; i < PAGE_COUNT; i++) {
                pages.add(Component.translatable("synbio.guide.page." + i));
            }
            opener.open(pages);
        }
        return InteractionResult.SUCCESS;
    }

    /** Client-only screen opener. Set during client init. */
    public interface GuideBookScreenOpener {
        void open(List<Component> pages);
    }
}
