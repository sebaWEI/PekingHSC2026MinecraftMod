package name.modid.item;

import name.modid.component.GeneticDesign;
import name.modid.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.stream.Collectors;
import java.util.function.Consumer;

public class EngineeredPlasmidItem extends Item {
	public EngineeredPlasmidItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
		GeneticDesign design = stack.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		if (!design.hasCds() && design.regulatoryParts().isEmpty()) {
			tooltip.accept(Component.translatable("tooltip.synbio.plasmid.empty").withStyle(ChatFormatting.GRAY));
			return;
		}

		String parts = design.regulatoryParts().stream()
			.map(p -> p.category().name().toLowerCase() + "_" + p.rarity().name().toLowerCase())
			.collect(Collectors.joining(", "));

		tooltip.accept(Component.translatable("tooltip.synbio.design.cds", design.cdsId().isBlank() ? "-" : design.cdsId().replace("synbio:", "")).withStyle(ChatFormatting.AQUA));
		tooltip.accept(Component.translatable("tooltip.synbio.design.multiplier", design.expressionMultiplier()).withStyle(ChatFormatting.GREEN));
		tooltip.accept(Component.translatable("tooltip.synbio.design.parts", parts).withStyle(ChatFormatting.GRAY));
	}
}

