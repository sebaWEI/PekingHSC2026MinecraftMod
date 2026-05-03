package name.modid.item;

import name.modid.component.GeneticDesign;
import name.modid.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class EngineeredEColiItem extends Item {
	public EngineeredEColiItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
		GeneticDesign design = stack.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		if (!design.hasCds() && design.regulatoryParts().isEmpty()) {
			return;
		}

		tooltip.accept(Component.translatable("tooltip.synbio.design.cds", design.cdsId().isBlank() ? "-" : design.cdsId().replace("synbio:", "")).withStyle(ChatFormatting.AQUA));
		tooltip.accept(Component.translatable("tooltip.synbio.design.multiplier", design.expressionMultiplier()).withStyle(ChatFormatting.GREEN));
	}
}

