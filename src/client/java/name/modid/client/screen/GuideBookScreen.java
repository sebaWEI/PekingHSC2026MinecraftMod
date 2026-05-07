package name.modid.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * Simple guide book screen with page-flipping.
 * Displays educational synthetic biology content from translatable components.
 */
public class GuideBookScreen extends Screen {
    private static final int PAGE_WIDTH = 180;
    private static final int PAGE_HEIGHT = 220;
    private static final int TEXT_WIDTH = 160;
    private static final int TEXT_X_OFFSET = 15;
    private static final int TEXT_Y_OFFSET = 20;
    private static final int LINE_HEIGHT = 10;
    private static final int BACKGROUND_COLOR = 0xCC_1A1A2E;
    private static final int PAGE_NUMBER_COLOR = 0xFF_888888;

    private final List<Component> pages;
    private int currentPage;
    private PageButton forwardButton;
    private PageButton backButton;
    private List<FormattedCharSequence> cachedLines;

    public GuideBookScreen(List<Component> pages) {
        super(Minecraft.getInstance(), Minecraft.getInstance().font, Component.translatable("synbio.guide.title"));
        this.pages = pages;
        this.currentPage = 0;
    }

    @Override
    protected void init() {
        super.init();

        int pageLeft = (this.width - PAGE_WIDTH) / 2;
        int pageBottom = (this.height - PAGE_HEIGHT) / 2 + PAGE_HEIGHT;

        // Page buttons at bottom corners
        this.backButton = this.addRenderableWidget(new PageButton(
            pageLeft + 20, pageBottom - 20, false,
            btn -> pageBack(), true
        ));

        this.forwardButton = this.addRenderableWidget(new PageButton(
            pageLeft + PAGE_WIDTH - 36, pageBottom - 20, true,
            btn -> pageForward(), true
        ));

        updateButtonVisibility();
        cacheCurrentPage();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Dark semi-transparent overlay over the world
        graphics.fill(RenderPipelines.GUI, 0, 0, this.width, this.height, 0xCC_101020);

        int x = (this.width - PAGE_WIDTH) / 2;
        int y = (this.height - PAGE_HEIGHT) / 2;

        // Page content
        if (cachedLines != null) {
            int textY = y + TEXT_Y_OFFSET;
            for (FormattedCharSequence line : cachedLines) {
                if (textY + LINE_HEIGHT > y + PAGE_HEIGHT - 30) break;
                graphics.text(this.font, line, x + TEXT_X_OFFSET, textY, 0xFF_DDDDDD);
                textY += LINE_HEIGHT;
            }
        }

        // Page number at bottom center
        String pageInfo = (currentPage + 1) + " / " + pages.size();
        int pageInfoWidth = this.font.width(pageInfo);
        graphics.text(this.font, pageInfo,
            x + (PAGE_WIDTH - pageInfoWidth) / 2, y + PAGE_HEIGHT - 16, PAGE_NUMBER_COLOR);

        // Title at top center
        Component title = getTitle();
        int titleWidth = this.font.width(title);
        graphics.text(this.font, title,
            x + (PAGE_WIDTH - titleWidth) / 2, y + 5, 0xFF_FFD700);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private void pageBack() {
        if (currentPage > 0) {
            currentPage--;
            cacheCurrentPage();
            updateButtonVisibility();
        }
    }

    private void pageForward() {
        if (currentPage < pages.size() - 1) {
            currentPage++;
            cacheCurrentPage();
            updateButtonVisibility();
        }
    }

    private void updateButtonVisibility() {
        if (backButton != null) backButton.visible = currentPage > 0;
        if (forwardButton != null) forwardButton.visible = currentPage < pages.size() - 1;
    }

    private void cacheCurrentPage() {
        if (currentPage >= 0 && currentPage < pages.size()) {
            Component pageContent = pages.get(currentPage);
            this.cachedLines = this.font.split(pageContent, TEXT_WIDTH);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
