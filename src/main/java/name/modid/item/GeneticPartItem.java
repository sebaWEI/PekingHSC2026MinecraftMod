package name.modid.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class GeneticPartItem extends Item {
	private final GeneticPartDefinition definition;

	public GeneticPartItem(Properties properties, GeneticPartDefinition definition) {
		super(properties);
		this.definition = definition;
	}

	public GeneticPartDefinition getDefinition() {
		return this.definition;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
		if (this.definition.isCds()) {
			tooltip.accept(Component.translatable("tooltip.synbio.part.cds").withStyle(ChatFormatting.AQUA));
			String geneName = this.definition.productId().replace("synbio:", "");
			tooltip.accept(Component.literal(geneName).withStyle(ChatFormatting.GRAY));
			return;
		}

		String category = this.definition.category() == null ? "-" : this.definition.category().name().toLowerCase();
		String rarity = this.definition.rarity() == null ? "-" : this.definition.rarity().name().toLowerCase();
		int add = this.definition.rarity() == null ? 0 : this.definition.rarity().additiveValue();

		tooltip.accept(Component.translatable("tooltip.synbio.part.kind", category).withStyle(ChatFormatting.AQUA));
		tooltip.accept(Component.translatable("tooltip.synbio.part.rarity", rarity).withStyle(ChatFormatting.LIGHT_PURPLE));
		tooltip.accept(Component.translatable("tooltip.synbio.part.multiplier_add", add).withStyle(ChatFormatting.GREEN));
	}
}
