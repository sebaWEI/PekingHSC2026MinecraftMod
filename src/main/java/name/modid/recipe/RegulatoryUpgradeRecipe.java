package name.modid.recipe;

import name.modid.component.PartCategory;
import name.modid.component.RarityTier;
import name.modid.item.GeneticPartDefinition;
import name.modid.item.GeneticPartItem;
import name.modid.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;

/**
 * 9-to-1 调节元件升级：绿→蓝、蓝→紫、紫→金。
 * 同类定输出类别；混类用输入物品哈希确定类别，同配置永远同结果。
 */
public class RegulatoryUpgradeRecipe extends CustomRecipe {
	private static final ThreadLocal<PartCategory> RESULT_CATEGORY = new ThreadLocal<>();

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ArrayList<GeneticPartDefinition> defs = getNineSameRarityRegulatoryParts(input);
		if (defs == null) return false;

		RarityTier in = defs.get(0).rarity();
		if (in != RarityTier.GREEN && in != RarityTier.BLUE && in != RarityTier.PURPLE) return false;

		EnumSet<PartCategory> categories = EnumSet.noneOf(PartCategory.class);
		for (GeneticPartDefinition def : defs) {
			categories.add(def.category());
		}

		if (categories.size() == 1) {
			RESULT_CATEGORY.set(categories.iterator().next());
			return true;
		}

		// Mixed categories: hash input items for deterministic output.
		// Same configuration of items always produces the same category.
		PartCategory out = hashPickCategory(input);
		RESULT_CATEGORY.set(out);
		return true;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		RarityTier inRarity = null;
		ArrayList<GeneticPartDefinition> defs = getNineSameRarityRegulatoryParts(input);
		if (defs == null || defs.isEmpty()) return ItemStack.EMPTY;
		inRarity = defs.get(0).rarity();

		RarityTier outRarity = switch (inRarity) {
			case GREEN -> RarityTier.BLUE;
			case BLUE -> RarityTier.PURPLE;
			case PURPLE -> RarityTier.GOLD;
			default -> null;
		};
		if (outRarity == null) return ItemStack.EMPTY;

		PartCategory outCategory = RESULT_CATEGORY.get();
		if (outCategory == null) {
			outCategory = hashPickCategory(input);
		}

		RESULT_CATEGORY.remove();
		Item out = ModItems.getRegulatoryPartItem(outCategory, outRarity);
		return out == null ? ItemStack.EMPTY : new ItemStack(out);
	}

	/**
	 * Deterministic category pick based on input item types and positions.
	 * Same items in same slots always give the same category.
	 */
	private static PartCategory hashPickCategory(CraftingInput input) {
		int h = 1;
		for (int i = 0; i < input.size(); i++) {
			ItemStack s = input.getItem(i);
			if (!s.isEmpty()) h = 31 * h + Objects.hashCode(s.getItem());
		}
		int idx = Math.floorMod(h, PartCategory.values().length);
		return PartCategory.values()[idx];
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return ModRecipes.REGULATORY_UPGRADE_SERIALIZER;
	}

	private static ArrayList<GeneticPartDefinition> getNineSameRarityRegulatoryParts(CraftingInput input) {
		ArrayList<GeneticPartDefinition> defs = new ArrayList<>();
		RarityTier common = null;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (!(stack.getItem() instanceof GeneticPartItem partItem)) return null;
			GeneticPartDefinition def = partItem.getDefinition();
			if (def.isCds()) return null;
			if (def.category() == null || def.rarity() == null) return null;

			if (common == null) {
				common = def.rarity();
			} else if (def.rarity() != common) {
				return null;
			}

			defs.add(def);
		}

		return defs.size() == 9 ? defs : null;
	}
}
