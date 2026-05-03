package name.modid.recipe;

import com.mojang.serialization.MapCodec;
import name.modid.IgemMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public final class ModRecipes {
	private ModRecipes() {
	}

	public static final RecipeSerializer<TransformationRecipe> TRANSFORMATION_SERIALIZER = register(
		"transformation",
		specialSerializer(TransformationRecipe::new)
	);

	public static final RecipeSerializer<ExpressionRecipe> EXPRESSION_SERIALIZER = register(
		"expression",
		specialSerializer(ExpressionRecipe::new)
	);

	public static final RecipeSerializer<RegulatoryUpgradeRecipe> REGULATORY_UPGRADE_SERIALIZER = register(
		"regulatory_upgrade",
		specialSerializer(RegulatoryUpgradeRecipe::new)
	);

	public static final RecipeSerializer<TissueDiscoveryRecipe> TISSUE_DISCOVERY_SERIALIZER = register(
		"tissue_discovery",
		specialSerializer(TissueDiscoveryRecipe::new)
	);

	public static final RecipeSerializer<BioEnhancementSmithingRecipe> BIO_ENHANCE_SMITHING_SERIALIZER = register(
		"bio_enhance_smithing",
		unitSerializer(BioEnhancementSmithingRecipe::new)
	);

	private static <T extends CustomRecipe> RecipeSerializer<T> specialSerializer(Supplier<T> factory) {
		T recipe = factory.get();
		return new RecipeSerializer<T>(MapCodec.unit(recipe), StreamCodec.unit(recipe));
	}

	private static <T extends Recipe<?>> RecipeSerializer<T> unitSerializer(Supplier<T> factory) {
		T recipe = factory.get();
		return new RecipeSerializer<T>(MapCodec.unit(recipe), StreamCodec.unit(recipe));
	}

	private static <T extends Recipe<?>> RecipeSerializer<T> register(String path, RecipeSerializer<T> serializer) {
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, path), serializer);
	}

	public static void initialize() {
		IgemMod.LOGGER.info("Mod recipes ready.");
	}
}
