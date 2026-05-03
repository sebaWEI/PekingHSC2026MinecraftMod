package name.modid.recipe;

import name.modid.IgemMod;
import name.modid.component.ModDataComponents;
import name.modid.component.BioEnhancements;
import name.modid.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleSmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.tags.ItemTags;

import java.util.List;
import java.util.Optional;

/**
 * Smithing-table bio enhancements for all 7 functional proteins (GFP is now a diagnostic tool, not smithing).
 *
 * Implemented enhancements:
 * - Myosin on Sword   → +attack damage (1/3/5/10)
 * - Myosin on Boots   → +movement speed (3%/5%/8%/12%)
 * - Keratin on Armor  → +toughness, +knockback resist, +armor (T3/T4)
 * - Telomerase on Damageable → Unbreaking / Mending / Unbreakable
 * - Spider Silk on Bow/Crossbow → Power enchantment
 * - Luciferase on Helmet → night vision (applied via tick handler)
 * - Ice Nucleation on Boots → Frost Walker enchantment
 *
 * Template slot is unused (may be empty).
 */
public class BioEnhancementSmithingRecipe extends SimpleSmithingRecipe {
	private static final Ingredient BASE = Ingredient.of(
		Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
		Items.DIAMOND_HELMET, Items.NETHERITE_HELMET,
		Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE,
		Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS,
		Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS,
		Items.DIAMOND_AXE, Items.NETHERITE_AXE,
		Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE,
		Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL,
		Items.DIAMOND_HOE, Items.NETHERITE_HOE,
		Items.BOW, Items.CROSSBOW
	);
	private static final Ingredient ADDITION = Ingredient.of(
		// Myosin: sword + boots
		ModItems.PROTEIN_MYOSIN_TIER1, ModItems.PROTEIN_MYOSIN_TIER2, ModItems.PROTEIN_MYOSIN_TIER3, ModItems.PROTEIN_MYOSIN_TIER4,
		// Telomerase: any damageable item
		ModItems.PROTEIN_TELOMERASE_TIER1, ModItems.PROTEIN_TELOMERASE_TIER2, ModItems.PROTEIN_TELOMERASE_TIER3, ModItems.PROTEIN_TELOMERASE_TIER4,
		// Keratin: armor
		ModItems.PROTEIN_KERATIN_TIER1, ModItems.PROTEIN_KERATIN_TIER2, ModItems.PROTEIN_KERATIN_TIER3, ModItems.PROTEIN_KERATIN_TIER4,
		// Spider Silk: bow/crossbow
		ModItems.PROTEIN_SPIDER_SILK_TIER1, ModItems.PROTEIN_SPIDER_SILK_TIER2, ModItems.PROTEIN_SPIDER_SILK_TIER3, ModItems.PROTEIN_SPIDER_SILK_TIER4,
		// Luciferase: helmet
		ModItems.PROTEIN_LUCIFERASE_TIER1, ModItems.PROTEIN_LUCIFERASE_TIER2, ModItems.PROTEIN_LUCIFERASE_TIER3, ModItems.PROTEIN_LUCIFERASE_TIER4,
		// Ice Nucleation: boots
		ModItems.PROTEIN_ICE_NUCLEATION_TIER1, ModItems.PROTEIN_ICE_NUCLEATION_TIER2, ModItems.PROTEIN_ICE_NUCLEATION_TIER3, ModItems.PROTEIN_ICE_NUCLEATION_TIER4
	);

	// Thread-local holders for enchantment lookups
	private static final ThreadLocal<Holder<Enchantment>> MENDING_HOLDER = new ThreadLocal<>();
	private static final ThreadLocal<Holder<Enchantment>> UNBREAKING_HOLDER = new ThreadLocal<>();
	private static final ThreadLocal<Holder<Enchantment>> POWER_HOLDER = new ThreadLocal<>();
	private static final ThreadLocal<Holder<Enchantment>> FROST_WALKER_HOLDER = new ThreadLocal<>();

	public BioEnhancementSmithingRecipe() {
		super(new Recipe.CommonInfo(true));
	}

	@Override
	public Optional<Ingredient> templateIngredient() {
		return Optional.empty();
	}

	@Override
	public Ingredient baseIngredient() {
		return BASE;
	}

	@Override
	public Optional<Ingredient> additionIngredient() {
		return Optional.of(ADDITION);
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.createFromOptionals(List.of(
			Optional.empty(),
			Optional.of(BASE),
			Optional.of(ADDITION)
		));
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		ItemStack base = input.base();
		ItemStack addition = input.addition();

		if (base.isEmpty() || addition.isEmpty()) return false;

		BioEnhancements enhancements = base.getOrDefault(ModDataComponents.BIO_ENHANCEMENTS, BioEnhancements.EMPTY);
		Registry<Enchantment> reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

		// --- Myosin: Sword (attack damage) ---
		if (isSword(base) && isMyosinProtein(addition)) {
			clearAllHolders();
			return enhancements.myosinTier() < 4;
		}

		// --- Myosin: Boots (movement speed) ---
		if (isBoots(base) && isMyosinProtein(addition)) {
			clearAllHolders();
			return enhancements.myosinTier() < 4;
		}

		// --- Telomerase: any damageable item (durability) ---
		if (base.isDamageableItem() && isTelomeraseProtein(addition)) {
			MENDING_HOLDER.set(reg.get(Enchantments.MENDING.identifier()).orElse(null));
			UNBREAKING_HOLDER.set(reg.get(Enchantments.UNBREAKING.identifier()).orElse(null));
			POWER_HOLDER.remove();
			FROST_WALKER_HOLDER.remove();
			return enhancements.telomeraseTier() < 4;
		}

		// --- Keratin: Armor (toughness + knockback + armor) ---
		if (isArmor(base) && isKeratinProtein(addition)) {
			clearAllHolders();
			return enhancements.keratinTier() < 4;
		}

		// --- Spider Silk: Bow/Crossbow (Power enchantment) ---
		if (isBowOrCrossbow(base) && isSpiderSilkProtein(addition)) {
			POWER_HOLDER.set(reg.get(Enchantments.POWER.identifier()).orElse(null));
			MENDING_HOLDER.remove();
			UNBREAKING_HOLDER.remove();
			FROST_WALKER_HOLDER.remove();
			return enhancements.spiderSilkTier() < 4;
		}

		// --- Luciferase: Helmet (night vision, applied via tick handler) ---
		if (isHelmet(base) && isLuciferaseProtein(addition)) {
			clearAllHolders();
			return enhancements.luciferaseTier() < 4;
		}

		// --- Ice Nucleation: Boots (Frost Walker enchantment) ---
		if (isBoots(base) && isIceNucleationProtein(addition)) {
			FROST_WALKER_HOLDER.set(reg.get(Enchantments.FROST_WALKER.identifier()).orElse(null));
			MENDING_HOLDER.remove();
			UNBREAKING_HOLDER.remove();
			POWER_HOLDER.remove();
			return enhancements.iceNucleationTier() < 4;
		}

		return false;
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input) {
		ItemStack base = input.base();
		ItemStack addition = input.addition();
		if (base.isEmpty() || addition.isEmpty()) return ItemStack.EMPTY;
		BioEnhancements enhancements = base.getOrDefault(ModDataComponents.BIO_ENHANCEMENTS, BioEnhancements.EMPTY);

		// --- Myosin: Sword (attack damage) ---
		if (isSword(base) && isMyosinProtein(addition)) {
			ItemStack result = base.copyWithCount(1);
			applyBioLore(result);

			ItemAttributeModifiers existing = result.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
			double bonus = myosinAttackBonus(addition);
			Identifier id = Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "bio_myosin_attack_" + bonus);
			AttributeModifier mod = new AttributeModifier(id, bonus, AttributeModifier.Operation.ADD_VALUE);
			ItemAttributeModifiers next = existing.withModifierAdded(Attributes.ATTACK_DAMAGE, mod, EquipmentSlotGroup.MAINHAND);
			result.set(DataComponents.ATTRIBUTE_MODIFIERS, next);
			result.set(ModDataComponents.BIO_ENHANCEMENTS, enhancements.withMyosinTier(myosinTier(addition)));
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			return result;
		}

		// --- Myosin: Boots (movement speed) ---
		if (isBoots(base) && isMyosinProtein(addition)) {
			ItemStack result = base.copyWithCount(1);
			applyBioLore(result);

			ItemAttributeModifiers existing = result.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
			double speedMult = myosinSpeedBonus(addition);
			// Base speed is 0.1; we add a fraction of that
			double speedAdd = 0.1 * speedMult;
			Identifier id = Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "bio_myosin_speed_" + speedMult);
			AttributeModifier mod = new AttributeModifier(id, speedAdd, AttributeModifier.Operation.ADD_VALUE);
			ItemAttributeModifiers next = existing.withModifierAdded(Attributes.MOVEMENT_SPEED, mod, EquipmentSlotGroup.FEET);
			result.set(DataComponents.ATTRIBUTE_MODIFIERS, next);
			result.set(ModDataComponents.BIO_ENHANCEMENTS, enhancements.withMyosinTier(myosinTier(addition)));
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			return result;
		}

		// --- Telomerase: any damageable item ---
		if (base.isDamageableItem() && isTelomeraseProtein(addition)) {
			ItemStack result = base.copyWithCount(1);
			applyBioLore(result);

			Holder<Enchantment> mending = MENDING_HOLDER.get();
			Holder<Enchantment> unbreaking = UNBREAKING_HOLDER.get();

			int tier = telomeraseTier(addition);
			if (tier >= 4) {
				result.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
			} else {
				int unbreakingLevel = telomeraseUnbreakingLevel(addition);
				if (unbreaking != null && unbreakingLevel > 0) result.enchant(unbreaking, unbreakingLevel);
			}

			if (mending != null && telomeraseAddsMending(addition)) result.enchant(mending, 1);

			result.set(ModDataComponents.BIO_ENHANCEMENTS, enhancements.withTelomeraseTier(tier));
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			return result;
		}

		// --- Keratin: Armor (toughness + knockback resist + armor boost) ---
		if (isArmor(base) && isKeratinProtein(addition)) {
			ItemStack result = base.copyWithCount(1);
			applyBioLore(result);

			double toughness = keratinToughnessBonus(addition);
			double kbRes = keratinKnockbackResBonus(addition);
			double armorBoost = keratinArmorBonus(addition);

			EquipmentSlotGroup slotGroup = getArmorSlotGroup(base);
			ItemAttributeModifiers existing = result.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
			ItemAttributeModifiers next = existing;

			if (toughness > 0) {
				Identifier id = Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "bio_keratin_toughness_" + toughness);
				AttributeModifier mod = new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE);
				next = next.withModifierAdded(Attributes.ARMOR_TOUGHNESS, mod, slotGroup);
			}
			if (kbRes > 0) {
				Identifier id = Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "bio_keratin_knockback_" + kbRes);
				AttributeModifier mod = new AttributeModifier(id, kbRes, AttributeModifier.Operation.ADD_VALUE);
				next = next.withModifierAdded(Attributes.KNOCKBACK_RESISTANCE, mod, slotGroup);
			}
			if (armorBoost > 0) {
				Identifier id = Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, "bio_keratin_armor_" + armorBoost);
				AttributeModifier mod = new AttributeModifier(id, armorBoost, AttributeModifier.Operation.ADD_VALUE);
				next = next.withModifierAdded(Attributes.ARMOR, mod, slotGroup);
			}

			result.set(DataComponents.ATTRIBUTE_MODIFIERS, next);
			result.set(ModDataComponents.BIO_ENHANCEMENTS, enhancements.withKeratinTier(keratinTier(addition)));
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			return result;
		}

		// --- Spider Silk: Bow/Crossbow (Power enchantment) ---
		if (isBowOrCrossbow(base) && isSpiderSilkProtein(addition)) {
			ItemStack result = base.copyWithCount(1);
			applyBioLore(result);

			Holder<Enchantment> power = POWER_HOLDER.get();
			int level = spiderSilkPowerLevel(addition);
			if (power != null && level > 0) {
				result.enchant(power, level);
			}

			result.set(ModDataComponents.BIO_ENHANCEMENTS, enhancements.withSpiderSilkTier(spiderSilkTier(addition)));
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			return result;
		}

		// --- Luciferase: Helmet (night vision via tick handler) ---
		if (isHelmet(base) && isLuciferaseProtein(addition)) {
			ItemStack result = base.copyWithCount(1);
			applyBioLore(result);

			result.set(ModDataComponents.BIO_ENHANCEMENTS, enhancements.withLuciferaseTier(luciferaseTier(addition)));
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			return result;
		}

		// --- Ice Nucleation: Boots (Frost Walker enchantment) ---
		if (isBoots(base) && isIceNucleationProtein(addition)) {
			ItemStack result = base.copyWithCount(1);
			applyBioLore(result);

			Holder<Enchantment> frostWalker = FROST_WALKER_HOLDER.get();
			int level = iceNucleationFrostWalkerLevel(addition);
			if (frostWalker != null && level > 0) {
				result.enchant(frostWalker, level);
			}

			result.set(ModDataComponents.BIO_ENHANCEMENTS, enhancements.withIceNucleationTier(iceNucleationTier(addition)));
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			return result;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<? extends SimpleSmithingRecipe> getSerializer() {
		return ModRecipes.BIO_ENHANCE_SMITHING_SERIALIZER;
	}

	// ── Helpers ──────────────────────────────────────────────

	private static void applyBioLore(ItemStack stack) {
		ItemLore lore = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
		ItemLore next = lore.withLineAdded(Component.translatable("tooltip.synbio.bio_enhanced"));
		stack.set(DataComponents.LORE, next);
	}

	private static void clearAllHolders() {
		MENDING_HOLDER.remove();
		UNBREAKING_HOLDER.remove();
		POWER_HOLDER.remove();
		FROST_WALKER_HOLDER.remove();
	}

	// ── Item type checks ─────────────────────────────────────

	private static boolean isSword(ItemStack stack) {
		return stack.is(ItemTags.SWORDS);
	}

	private static boolean isArmor(ItemStack stack) {
		return isHelmet(stack) || isChestplate(stack) || isLeggings(stack) || isBoots(stack);
	}

	private static boolean isBowOrCrossbow(ItemStack stack) {
		return stack.is(Items.BOW) || stack.is(Items.CROSSBOW);
	}

	// ── Armor slot helpers ───────────────────────────────────

	private static EquipmentSlotGroup getArmorSlotGroup(ItemStack stack) {
		EquipmentSlot slot = getArmorEquipmentSlot(stack);
		return slot == null ? EquipmentSlotGroup.ARMOR : EquipmentSlotGroup.bySlot(slot);
	}

	private static EquipmentSlot getArmorEquipmentSlot(ItemStack stack) {
		if (isHelmet(stack)) return EquipmentSlot.HEAD;
		if (isChestplate(stack)) return EquipmentSlot.CHEST;
		if (isLeggings(stack)) return EquipmentSlot.LEGS;
		if (isBoots(stack)) return EquipmentSlot.FEET;
		return null;
	}

	private static boolean isHelmet(ItemStack stack) {
		return stack.is(Items.LEATHER_HELMET) || stack.is(Items.CHAINMAIL_HELMET)
			|| stack.is(Items.IRON_HELMET) || stack.is(Items.GOLDEN_HELMET)
			|| stack.is(Items.DIAMOND_HELMET) || stack.is(Items.NETHERITE_HELMET);
	}

	private static boolean isChestplate(ItemStack stack) {
		return stack.is(Items.LEATHER_CHESTPLATE) || stack.is(Items.CHAINMAIL_CHESTPLATE)
			|| stack.is(Items.IRON_CHESTPLATE) || stack.is(Items.GOLDEN_CHESTPLATE)
			|| stack.is(Items.DIAMOND_CHESTPLATE) || stack.is(Items.NETHERITE_CHESTPLATE);
	}

	private static boolean isLeggings(ItemStack stack) {
		return stack.is(Items.LEATHER_LEGGINGS) || stack.is(Items.CHAINMAIL_LEGGINGS)
			|| stack.is(Items.IRON_LEGGINGS) || stack.is(Items.GOLDEN_LEGGINGS)
			|| stack.is(Items.DIAMOND_LEGGINGS) || stack.is(Items.NETHERITE_LEGGINGS);
	}

	private static boolean isBoots(ItemStack stack) {
		return stack.is(Items.LEATHER_BOOTS) || stack.is(Items.CHAINMAIL_BOOTS)
			|| stack.is(Items.IRON_BOOTS) || stack.is(Items.GOLDEN_BOOTS)
			|| stack.is(Items.DIAMOND_BOOTS) || stack.is(Items.NETHERITE_BOOTS);
	}

	// ── Protein type checks ──────────────────────────────────

	private static boolean isMyosinProtein(ItemStack stack) {
		return stack.is(ModItems.PROTEIN_MYOSIN_TIER1) || stack.is(ModItems.PROTEIN_MYOSIN_TIER2)
			|| stack.is(ModItems.PROTEIN_MYOSIN_TIER3) || stack.is(ModItems.PROTEIN_MYOSIN_TIER4);
	}

	private static boolean isTelomeraseProtein(ItemStack stack) {
		return stack.is(ModItems.PROTEIN_TELOMERASE_TIER1) || stack.is(ModItems.PROTEIN_TELOMERASE_TIER2)
			|| stack.is(ModItems.PROTEIN_TELOMERASE_TIER3) || stack.is(ModItems.PROTEIN_TELOMERASE_TIER4);
	}

	private static boolean isKeratinProtein(ItemStack stack) {
		return stack.is(ModItems.PROTEIN_KERATIN_TIER1) || stack.is(ModItems.PROTEIN_KERATIN_TIER2)
			|| stack.is(ModItems.PROTEIN_KERATIN_TIER3) || stack.is(ModItems.PROTEIN_KERATIN_TIER4);
	}

	private static boolean isSpiderSilkProtein(ItemStack stack) {
		return stack.is(ModItems.PROTEIN_SPIDER_SILK_TIER1) || stack.is(ModItems.PROTEIN_SPIDER_SILK_TIER2)
			|| stack.is(ModItems.PROTEIN_SPIDER_SILK_TIER3) || stack.is(ModItems.PROTEIN_SPIDER_SILK_TIER4);
	}

	private static boolean isLuciferaseProtein(ItemStack stack) {
		return stack.is(ModItems.PROTEIN_LUCIFERASE_TIER1) || stack.is(ModItems.PROTEIN_LUCIFERASE_TIER2)
			|| stack.is(ModItems.PROTEIN_LUCIFERASE_TIER3) || stack.is(ModItems.PROTEIN_LUCIFERASE_TIER4);
	}

	private static boolean isIceNucleationProtein(ItemStack stack) {
		return stack.is(ModItems.PROTEIN_ICE_NUCLEATION_TIER1) || stack.is(ModItems.PROTEIN_ICE_NUCLEATION_TIER2)
			|| stack.is(ModItems.PROTEIN_ICE_NUCLEATION_TIER3) || stack.is(ModItems.PROTEIN_ICE_NUCLEATION_TIER4);
	}

	// ── Myosin: tier + attack bonus ─────────────────────────

	private static int myosinTier(ItemStack addition) {
		if (addition.is(ModItems.PROTEIN_MYOSIN_TIER4)) return 4;
		if (addition.is(ModItems.PROTEIN_MYOSIN_TIER3)) return 3;
		if (addition.is(ModItems.PROTEIN_MYOSIN_TIER2)) return 2;
		return 1;
	}

	private static double myosinAttackBonus(ItemStack addition) {
		return switch (myosinTier(addition)) {
			case 4 -> 10.0;
			case 3 -> 5.0;
			case 2 -> 3.0;
			default -> 1.0;
		};
	}

	private static double myosinSpeedBonus(ItemStack addition) {
		// Returns multiplier (0.03 / 0.05 / 0.08 / 0.12) applied to base speed 0.1
		return switch (myosinTier(addition)) {
			case 4 -> 0.12;
			case 3 -> 0.08;
			case 2 -> 0.05;
			default -> 0.03;
		};
	}

	// ── Telomerase: tier + unbreaking ───────────────────────

	private static int telomeraseTier(ItemStack addition) {
		if (addition.is(ModItems.PROTEIN_TELOMERASE_TIER4)) return 4;
		if (addition.is(ModItems.PROTEIN_TELOMERASE_TIER3)) return 3;
		if (addition.is(ModItems.PROTEIN_TELOMERASE_TIER2)) return 2;
		return 1;
	}

	private static int telomeraseUnbreakingLevel(ItemStack addition) {
		return switch (telomeraseTier(addition)) {
			case 4 -> 3;
			case 3 -> 2;
			case 2 -> 1;
			default -> 0;
		};
	}

	private static boolean telomeraseAddsMending(ItemStack addition) {
		return addition.is(ModItems.PROTEIN_TELOMERASE_TIER4);
	}

	// ── Keratin: tier + toughness + knockback + armor ───────

	private static int keratinTier(ItemStack addition) {
		if (addition.is(ModItems.PROTEIN_KERATIN_TIER4)) return 4;
		if (addition.is(ModItems.PROTEIN_KERATIN_TIER3)) return 3;
		if (addition.is(ModItems.PROTEIN_KERATIN_TIER2)) return 2;
		return 1;
	}

	private static double keratinToughnessBonus(ItemStack addition) {
		return switch (keratinTier(addition)) {
			case 4 -> 3.0;
			case 3 -> 2.0;
			case 2 -> 1.0;
			default -> 0.5;
		};
	}

	private static double keratinKnockbackResBonus(ItemStack addition) {
		return switch (keratinTier(addition)) {
			case 4 -> 0.20;
			case 3 -> 0.15;
			case 2 -> 0.10;
			default -> 0.05;
		};
	}

	private static double keratinArmorBonus(ItemStack addition) {
		// Only T3/T4 provide base armor boost
		return switch (keratinTier(addition)) {
			case 4 -> 2.0;
			case 3 -> 1.0;
			default -> 0.0;
		};
	}

	// ── Spider Silk: tier + Power level ─────────────────────

	private static int spiderSilkTier(ItemStack addition) {
		if (addition.is(ModItems.PROTEIN_SPIDER_SILK_TIER4)) return 4;
		if (addition.is(ModItems.PROTEIN_SPIDER_SILK_TIER3)) return 3;
		if (addition.is(ModItems.PROTEIN_SPIDER_SILK_TIER2)) return 2;
		return 1;
	}

	private static int spiderSilkPowerLevel(ItemStack addition) {
		return switch (spiderSilkTier(addition)) {
			case 4 -> 4;
			case 3 -> 3;
			case 2 -> 2;
			default -> 1;
		};
	}

	// ── Luciferase: tier ────────────────────────────────────

	private static int luciferaseTier(ItemStack addition) {
		if (addition.is(ModItems.PROTEIN_LUCIFERASE_TIER4)) return 4;
		if (addition.is(ModItems.PROTEIN_LUCIFERASE_TIER3)) return 3;
		if (addition.is(ModItems.PROTEIN_LUCIFERASE_TIER2)) return 2;
		return 1;
	}

	// ── Ice Nucleation: tier + Frost Walker level ────────────

	private static int iceNucleationTier(ItemStack addition) {
		if (addition.is(ModItems.PROTEIN_ICE_NUCLEATION_TIER4)) return 4;
		if (addition.is(ModItems.PROTEIN_ICE_NUCLEATION_TIER3)) return 3;
		if (addition.is(ModItems.PROTEIN_ICE_NUCLEATION_TIER2)) return 2;
		return 1;
	}

	private static int iceNucleationFrostWalkerLevel(ItemStack addition) {
		return switch (iceNucleationTier(addition)) {
			case 4 -> 2;
			case 3 -> 2;
			case 2 -> 1;
			default -> 1;
		};
	}
}
