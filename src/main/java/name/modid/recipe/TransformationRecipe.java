package name.modid.recipe;

import name.modid.component.GeneticDesign;
import name.modid.component.ModDataComponents;
import name.modid.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TransformationRecipe extends CustomRecipe {
	public TransformationRecipe() {
		super();
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack competent = ItemStack.EMPTY;
		ItemStack plasmid = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.is(ModItems.E_COLI_COMPETENT)) {
				if (!competent.isEmpty()) return false;
				competent = stack;
				continue;
			}

			if (stack.is(ModItems.ENGINEERED_PLASMID)) {
				if (!plasmid.isEmpty()) return false;
				plasmid = stack;
				continue;
			}

			return false;
		}

		if (competent.isEmpty() || plasmid.isEmpty()) return false;
		GeneticDesign design = plasmid.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
		return design.hasCds();
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.is(ModItems.ENGINEERED_PLASMID)) continue;

			GeneticDesign design = stack.getOrDefault(ModDataComponents.GENETIC_DESIGN, GeneticDesign.EMPTY);
			if (!design.hasCds()) return ItemStack.EMPTY;

			ItemStack result = new ItemStack(ModItems.E_COLI_ENGINEERED);
			result.set(ModDataComponents.GENETIC_DESIGN, design);
			return result;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			// Return the engineered plasmid (it replicates inside the bacteria)
			if (stack.is(ModItems.ENGINEERED_PLASMID)) {
				remaining.set(i, stack.copyWithCount(1));
			}
		}
		return remaining;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return ModRecipes.TRANSFORMATION_SERIALIZER;
	}
}
