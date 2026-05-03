package name.modid.item;

import name.modid.component.GeneticDesign;
import name.modid.component.ModDataComponents;
import name.modid.component.ProteinTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * GFP protein — a diagnostic (reporter) tool for synthetic biology.
 *
 * Right-click in the air to scan your hotbar + offhand for engineered plasmids
 * and engineered E. coli, displaying their genetic design in chat.
 *
 * In real science, GFP (Green Fluorescent Protein, 2008 Nobel Prize) is the
 * most widely used reporter gene: green fluorescence = successful expression.
 */
public class GfpDiagnosticItem extends Item {

	public GfpDiagnosticItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		// Scan offhand first (intended target), fall back to hotbar
		ItemStack offhand = player.getOffhandItem();
		boolean found = scanAndReport(player, offhand, Component.translatable("tooltip.synbio.gfp.scan_offhand"));

		if (!found) {
			for (int i = 0; i < 9; i++) {
				ItemStack hotbarStack = player.getInventory().getItem(i);
				if (scanAndReport(player, hotbarStack, Component.literal("[" + (i + 1) + "]"))) {
					found = true;
					break;
				}
			}
		}

		if (!found) {
			player.sendSystemMessage(
				Component.translatable("tooltip.synbio.gfp.no_constructs"));
		}

		return InteractionResult.SUCCESS;
	}

	private static boolean scanAndReport(Player player, ItemStack stack, Component slotLabel) {
		if (stack.isEmpty()) return false;

		GeneticDesign design = stack.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		if (design.hasCds()) {
			ProteinTier tier = design.getProteinTier();
			String geneName = getGeneDisplayName(design.cdsId());
			player.sendSystemMessage(
				Component.translatable("tooltip.synbio.gfp.scan_result",
					slotLabel,
					geneName,
					design.expressionMultiplier(),
					tier.ordinal() + 1
				));
			return true;
		}

		return false;
	}

	private static String getGeneDisplayName(String cdsId) {
		if (cdsId == null) return "???";
		return switch (cdsId) {
			case "synbio:gfp" -> "GFP";
			case "synbio:keratin" -> "Keratin";
			case "synbio:myosin" -> "Myosin";
			case "synbio:telomerase" -> "Telomerase";
			case "synbio:spider_silk" -> "Spider Silk";
			case "synbio:luciferase" -> "Luciferase";
			case "synbio:petase" -> "PETase";
			case "synbio:ice_nucleation" -> "Ice Nucleation";
			default -> cdsId;
		};
	}
}
