package name.modid.recipe;

import name.modid.component.ModDataComponents;
import name.modid.component.PartCategory;
import name.modid.component.RarityTier;
import name.modid.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 1×{@link ModItems#UNKNOWN_BIOLOGICAL_TISSUE} → 随机调节元件。
 * <p>
 * Each tissue carries a hidden {@link ModDataComponents#TISSUE_IDENTITY} seed
 * that is lazily generated when the item first enters a player's inventory.
 * The seed deterministically maps to a (category, rarity) pair, so the same
 * tissue always produces the same result — no more flickering crafting preview.
 * <p>
 * Multiple tissues can be placed in the grid; each craft consumes exactly one.
 */
public class TissueDiscoveryRecipe extends CustomRecipe {

	/** Probability weights out of 10000: gold 200, purple 800, blue 2500, rest green. */
	private static final int WEIGHT_GOLD = 200;
	private static final int WEIGHT_PURPLE = 1000;   // 200 + 800
	private static final int WEIGHT_BLUE = 3500;     // 1000 + 2500

	private static final ThreadLocal<Item> RESULT = new ThreadLocal<>();

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int tissueCount = 0;
		ItemStack tissueStack = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;
			if (!stack.is(ModItems.UNKNOWN_BIOLOGICAL_TISSUE)) return false;
			tissueCount += stack.getCount();
			if (tissueStack.isEmpty()) tissueStack = stack;
		}

		if (tissueCount < 1) return false;

		// Read the first tissue's fixed identity.
		Integer seed = tissueStack.get(ModDataComponents.TISSUE_IDENTITY);
		if (seed != null) {
			int rarityIdx = seed / 10;
			int catIdx = seed % 10;
			RarityTier[] rarities = RarityTier.values();
			PartCategory[] cats = PartCategory.values();
			if (rarityIdx < 0 || rarityIdx >= rarities.length || catIdx < 0 || catIdx >= cats.length) {
				return false;
			}
			RarityTier rarity = rarities[rarityIdx];
			PartCategory category = cats[catIdx];
			Item out = ModItems.getRegulatoryPartItem(category, rarity);
			RESULT.set(out);
			return out != null;
		}

		// Legacy fallback: no identity component on stack — use deterministic tick-based roll.
		java.util.Random rng = new java.util.Random(level.getGameTime());
		int w = rng.nextInt(10000);
		RarityTier rarity;
		if (w < WEIGHT_GOLD) {
			rarity = RarityTier.GOLD;
		} else if (w < WEIGHT_PURPLE) {
			rarity = RarityTier.PURPLE;
		} else if (w < WEIGHT_BLUE) {
			rarity = RarityTier.BLUE;
		} else {
			rarity = RarityTier.GREEN;
		}
		int catIdx = rng.nextInt(PartCategory.values().length);
		Item out = ModItems.getRegulatoryPartItem(PartCategory.values()[catIdx], rarity);
		RESULT.set(out);
		return out != null;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		try {
			Item out = RESULT.get();
			return out == null ? ItemStack.EMPTY : new ItemStack(out);
		} finally {
			RESULT.remove();
		}
	}

	/**
	 * When multiple tissues are in the grid, only consume ONE per craft.
	 * Returns all input items except the first tissue, which is consumed.
	 */
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
		boolean consumedOne = false;
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.is(ModItems.UNKNOWN_BIOLOGICAL_TISSUE)) {
				if (!consumedOne) {
					// Consume one tissue: leave its slot empty (or with count-1)
					ItemStack reduced = stack.copy();
					reduced.shrink(1);
					remaining.set(i, reduced.isEmpty() ? ItemStack.EMPTY : reduced);
					consumedOne = true;
				} else {
					// Keep remaining tissues untouched
					remaining.set(i, stack.copy());
				}
			}
		}
		return remaining;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return ModRecipes.TISSUE_DISCOVERY_SERIALIZER;
	}
}
