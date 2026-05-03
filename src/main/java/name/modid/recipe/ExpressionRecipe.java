package name.modid.recipe;

import name.modid.component.GeneticDesign;
import name.modid.component.ProteinTier;
import name.modid.component.ModDataComponents;
import name.modid.item.GeneIds;
import name.modid.item.BrothTier;
import name.modid.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ExpressionRecipe extends CustomRecipe {
	public ExpressionRecipe() {
		super();
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack bacteria = ItemStack.EMPTY;
		ItemStack broth = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.is(ModItems.E_COLI_ENGINEERED)) {
				if (!bacteria.isEmpty()) return false;
				bacteria = stack;
				continue;
			}

			if (ModItems.getBrothTier(stack) != null) {
				if (!broth.isEmpty()) return false;
				broth = stack;
				continue;
			}

			return false;
		}

		if (bacteria.isEmpty() || broth.isEmpty()) return false;
		GeneticDesign design = bacteria.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		BrothTier brothTier = ModItems.getBrothTier(broth);
		if (brothTier == null) return false;
		if (!brothTier.allows(design.getProteinTier())) return false;
		return !getExpressionResult(design).isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.is(ModItems.E_COLI_ENGINEERED)) continue;

			GeneticDesign design = stack.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
			return getExpressionResult(design);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.is(ModItems.E_COLI_ENGINEERED)) {
				remaining.set(i, stack.copyWithCount(1));
			}
		}
		// Biosafety: every expression produces waste culture
		for (int i = 0; i < remaining.size(); i++) {
			if (remaining.get(i).isEmpty()) {
				remaining.set(i, new ItemStack(ModItems.WASTE_CULTURE));
				break;
			}
		}
		return remaining;
	}

	public static ItemStack getExpressionResult(GeneticDesign design) {
		if (!design.hasCds()) return ItemStack.EMPTY;

		ProteinTier tier = design.getProteinTier();
		String cds = design.cdsId();

		// CDS does not have rarity. Output depends on (CDS, ProteinTier).
		if (GeneIds.GFP.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_GFP_TIER1;
				case TIER_2 -> ModItems.PROTEIN_GFP_TIER2;
				case TIER_3 -> ModItems.PROTEIN_GFP_TIER3;
				case TIER_4 -> ModItems.PROTEIN_GFP_TIER4;
			});
		}
		if (GeneIds.KERATIN.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_KERATIN_TIER1;
				case TIER_2 -> ModItems.PROTEIN_KERATIN_TIER2;
				case TIER_3 -> ModItems.PROTEIN_KERATIN_TIER3;
				case TIER_4 -> ModItems.PROTEIN_KERATIN_TIER4;
			});
		}
		if (GeneIds.MYOSIN.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_MYOSIN_TIER1;
				case TIER_2 -> ModItems.PROTEIN_MYOSIN_TIER2;
				case TIER_3 -> ModItems.PROTEIN_MYOSIN_TIER3;
				case TIER_4 -> ModItems.PROTEIN_MYOSIN_TIER4;
			});
		}
		if (GeneIds.TELOMERASE.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_TELOMERASE_TIER1;
				case TIER_2 -> ModItems.PROTEIN_TELOMERASE_TIER2;
				case TIER_3 -> ModItems.PROTEIN_TELOMERASE_TIER3;
				case TIER_4 -> ModItems.PROTEIN_TELOMERASE_TIER4;
			});
		}
		if (GeneIds.SPIDER_SILK.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_SPIDER_SILK_TIER1;
				case TIER_2 -> ModItems.PROTEIN_SPIDER_SILK_TIER2;
				case TIER_3 -> ModItems.PROTEIN_SPIDER_SILK_TIER3;
				case TIER_4 -> ModItems.PROTEIN_SPIDER_SILK_TIER4;
			});
		}
		if (GeneIds.LUCIFERASE.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_LUCIFERASE_TIER1;
				case TIER_2 -> ModItems.PROTEIN_LUCIFERASE_TIER2;
				case TIER_3 -> ModItems.PROTEIN_LUCIFERASE_TIER3;
				case TIER_4 -> ModItems.PROTEIN_LUCIFERASE_TIER4;
			});
		}
		if (GeneIds.PETASE.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_PETASE_TIER1;
				case TIER_2 -> ModItems.PROTEIN_PETASE_TIER2;
				case TIER_3 -> ModItems.PROTEIN_PETASE_TIER3;
				case TIER_4 -> ModItems.PROTEIN_PETASE_TIER4;
			});
		}
		if (GeneIds.ICE_NUCLEATION.equals(cds)) {
			return new ItemStack(switch (tier) {
				case TIER_1 -> ModItems.PROTEIN_ICE_NUCLEATION_TIER1;
				case TIER_2 -> ModItems.PROTEIN_ICE_NUCLEATION_TIER2;
				case TIER_3 -> ModItems.PROTEIN_ICE_NUCLEATION_TIER3;
				case TIER_4 -> ModItems.PROTEIN_ICE_NUCLEATION_TIER4;
			});
		}

		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return ModRecipes.EXPRESSION_SERIALIZER;
	}
}
