package name.modid.item;

import name.modid.IgemMod;
import name.modid.component.PartCategory;
import name.modid.component.RarityTier;
import name.modid.component.ModDataComponents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * 1.21.2+ 需在 {@link Item.Properties} 上绑定 {@link ResourceKey}，否则运行时报 “Item id not set”。
 * 流程：{@code ResourceKey} → {@link Item.Properties#setId(ResourceKey)} → 构造 {@link Item} → {@link Registry#register}。
 */
public final class ModItems {
	private ModItems() {
	}

	/** Helper for recipes/loot: resolve a regulatory part item by category + rarity. */
	/** CDS id（如 {@link GeneIds#GFP}）→ 对应 {@link GeneticPartItem}；未知则 null。 */
	public static Item itemForCdsId(String cdsId) {
		if (cdsId == null || cdsId.isEmpty()) return null;
		if (GeneIds.GFP.equals(cdsId)) return DNA_PART_GFP;
		if (GeneIds.KERATIN.equals(cdsId)) return DNA_PART_CDS_KERATIN;
		if (GeneIds.MYOSIN.equals(cdsId)) return DNA_PART_CDS_MYOSIN;
		if (GeneIds.TELOMERASE.equals(cdsId)) return DNA_PART_CDS_TELOMERASE;
		if (GeneIds.SPIDER_SILK.equals(cdsId)) return DNA_PART_CDS_SPIDER_SILK;
		if (GeneIds.LUCIFERASE.equals(cdsId)) return DNA_PART_CDS_LUCIFERASE;
		if (GeneIds.PETASE.equals(cdsId)) return DNA_PART_CDS_PETASE;
		if (GeneIds.ICE_NUCLEATION.equals(cdsId)) return DNA_PART_CDS_ICE_NUCLEATION;
		return null;
	}

	public static Item getRegulatoryPartItem(PartCategory category, RarityTier rarity) {
		if (category == null || rarity == null) return null;

		return switch (category) {
			case PROMOTER -> switch (rarity) {
				case GREEN -> DNA_PART_PROMOTER;
				case BLUE -> DNA_PART_PROMOTER_BLUE;
				case PURPLE -> DNA_PART_PROMOTER_PURPLE;
				case GOLD -> DNA_PART_PROMOTER_GOLD;
			};
			case UTR3 -> switch (rarity) {
				case GREEN -> DNA_PART_UTR3;
				case BLUE -> DNA_PART_UTR3_BLUE;
				case PURPLE -> DNA_PART_UTR3_PURPLE;
				case GOLD -> DNA_PART_UTR3_GOLD;
			};
			case UTR5 -> switch (rarity) {
				case GREEN -> DNA_PART_UTR5;
				case BLUE -> DNA_PART_UTR5_BLUE;
				case PURPLE -> DNA_PART_UTR5_PURPLE;
				case GOLD -> DNA_PART_UTR5_GOLD;
			};
			case ENHANCER -> switch (rarity) {
				case GREEN -> DNA_PART_ENHANCER;
				case BLUE -> DNA_PART_ENHANCER_BLUE;
				case PURPLE -> DNA_PART_ENHANCER_PURPLE;
				case GOLD -> DNA_PART_ENHANCER_GOLD;
			};
			case SINEB2 -> switch (rarity) {
				case GREEN -> DNA_PART_SINEB2;
				case BLUE -> DNA_PART_SINEB2_BLUE;
				case PURPLE -> DNA_PART_SINEB2_PURPLE;
				case GOLD -> DNA_PART_SINEB2_GOLD;
			};
		};
	}

	public static final Item DNA_PART_PROMOTER = register(
		"dna_part_promoter",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.PROMOTER, RarityTier.GREEN)),
		new Item.Properties()
	);
	public static final Item DNA_PART_PROMOTER_BLUE = register(
		"dna_part_promoter_blue",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.PROMOTER, RarityTier.BLUE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_PROMOTER_PURPLE = register(
		"dna_part_promoter_purple",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.PROMOTER, RarityTier.PURPLE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_PROMOTER_GOLD = register(
		"dna_part_promoter_gold",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.PROMOTER, RarityTier.GOLD)),
		new Item.Properties()
	);

	public static final Item DNA_PART_UTR3 = register(
		"dna_part_utr3",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR3, RarityTier.GREEN)),
		new Item.Properties()
	);
	public static final Item DNA_PART_UTR3_BLUE = register(
		"dna_part_utr3_blue",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR3, RarityTier.BLUE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_UTR3_PURPLE = register(
		"dna_part_utr3_purple",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR3, RarityTier.PURPLE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_UTR3_GOLD = register(
		"dna_part_utr3_gold",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR3, RarityTier.GOLD)),
		new Item.Properties()
	);

	public static final Item DNA_PART_UTR5 = register(
		"dna_part_utr5",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR5, RarityTier.GREEN)),
		new Item.Properties()
	);
	public static final Item DNA_PART_UTR5_BLUE = register(
		"dna_part_utr5_blue",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR5, RarityTier.BLUE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_UTR5_PURPLE = register(
		"dna_part_utr5_purple",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR5, RarityTier.PURPLE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_UTR5_GOLD = register(
		"dna_part_utr5_gold",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.UTR5, RarityTier.GOLD)),
		new Item.Properties()
	);

	public static final Item DNA_PART_ENHANCER = register(
		"dna_part_enhancer",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.ENHANCER, RarityTier.GREEN)),
		new Item.Properties()
	);
	public static final Item DNA_PART_ENHANCER_BLUE = register(
		"dna_part_enhancer_blue",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.ENHANCER, RarityTier.BLUE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_ENHANCER_PURPLE = register(
		"dna_part_enhancer_purple",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.ENHANCER, RarityTier.PURPLE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_ENHANCER_GOLD = register(
		"dna_part_enhancer_gold",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.ENHANCER, RarityTier.GOLD)),
		new Item.Properties()
	);

	public static final Item DNA_PART_SINEB2 = register(
		"dna_part_sineb2",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.SINEB2, RarityTier.GREEN)),
		new Item.Properties()
	);
	public static final Item DNA_PART_SINEB2_BLUE = register(
		"dna_part_sineb2_blue",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.SINEB2, RarityTier.BLUE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_SINEB2_PURPLE = register(
		"dna_part_sineb2_purple",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.SINEB2, RarityTier.PURPLE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_SINEB2_GOLD = register(
		"dna_part_sineb2_gold",
		props -> new GeneticPartItem(props, GeneticPartDefinition.regulatory(PartCategory.SINEB2, RarityTier.GOLD)),
		new Item.Properties()
	);

	public static final Item DNA_PART_GFP = register(
		"dna_part_gfp",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.GFP)),
		new Item.Properties()
	);
	public static final Item DNA_PART_CDS_KERATIN = register(
		"dna_part_cds_keratin",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.KERATIN)),
		new Item.Properties()
	);
	public static final Item DNA_PART_CDS_MYOSIN = register(
		"dna_part_cds_myosin",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.MYOSIN)),
		new Item.Properties()
	);
	public static final Item DNA_PART_CDS_TELOMERASE = register(
		"dna_part_cds_telomerase",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.TELOMERASE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_CDS_SPIDER_SILK = register(
		"dna_part_cds_spider_silk",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.SPIDER_SILK)),
		new Item.Properties()
	);
	public static final Item DNA_PART_CDS_LUCIFERASE = register(
		"dna_part_cds_luciferase",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.LUCIFERASE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_CDS_PETASE = register(
		"dna_part_cds_petase",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.PETASE)),
		new Item.Properties()
	);
	public static final Item DNA_PART_CDS_ICE_NUCLEATION = register(
		"dna_part_cds_ice_nucleation",
		props -> new GeneticPartItem(props, GeneticPartDefinition.cds(GeneIds.ICE_NUCLEATION)),
		new Item.Properties()
	);

	public static final Item BLANK_PLASMID = register("blank_plasmid", Item::new, new Item.Properties().stacksTo(16));
	public static final Item ENGINEERED_PLASMID = register(
		"engineered_plasmid",
		EngineeredPlasmidItem::new,
		new Item.Properties().stacksTo(1).component(ModDataComponents.GENETIC_DESIGN, name.modid.component.GeneticDesign.EMPTY)
	);

	public static final Item CALCIUM_CHLORIDE = register("calcium_chloride", Item::new, new Item.Properties().stacksTo(64));

	public static final Item E_COLI_WILD = register("e_coli_wild", Item::new, new Item.Properties().stacksTo(16));
	public static final Item E_COLI_COMPETENT = register("e_coli_competent", Item::new, new Item.Properties().stacksTo(16));
	public static final Item E_COLI_ENGINEERED = register(
		"e_coli_engineered",
		EngineeredEColiItem::new,
		new Item.Properties().stacksTo(1).component(ModDataComponents.GENETIC_DESIGN, name.modid.component.GeneticDesign.EMPTY)
	);

	// Broth tiers: keep the original nutrient_broth as GREEN for backward compatibility.
	public static final Item NUTRIENT_BROTH = register("nutrient_broth", Item::new, new Item.Properties().stacksTo(64));
	public static final Item NUTRIENT_BROTH_GREEN = NUTRIENT_BROTH;
	public static final Item NUTRIENT_BROTH_BLUE = register("nutrient_broth_blue", Item::new, new Item.Properties().stacksTo(64));
	public static final Item NUTRIENT_BROTH_PURPLE = register("nutrient_broth_purple", Item::new, new Item.Properties().stacksTo(64));
	public static final Item NUTRIENT_BROTH_GOLD = register("nutrient_broth_gold", Item::new, new Item.Properties().stacksTo(64));
	public static final Item GFP_EXTRACT = register("gfp_extract", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_GFP_TIER1 = register("protein_gfp_tier1", GfpDiagnosticItem::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_GFP_TIER2 = register("protein_gfp_tier2", GfpDiagnosticItem::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_GFP_TIER3 = register("protein_gfp_tier3", GfpDiagnosticItem::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_GFP_TIER4 = register("protein_gfp_tier4", GfpDiagnosticItem::new, new Item.Properties().stacksTo(64));

	public static final Item PROTEIN_KERATIN_TIER1 = register("protein_keratin_tier1", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_KERATIN_TIER2 = register("protein_keratin_tier2", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_KERATIN_TIER3 = register("protein_keratin_tier3", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_KERATIN_TIER4 = register("protein_keratin_tier4", Item::new, new Item.Properties().stacksTo(64));

	public static final Item PROTEIN_MYOSIN_TIER1 = register("protein_myosin_tier1", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_MYOSIN_TIER2 = register("protein_myosin_tier2", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_MYOSIN_TIER3 = register("protein_myosin_tier3", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_MYOSIN_TIER4 = register("protein_myosin_tier4", Item::new, new Item.Properties().stacksTo(64));

	public static final Item PROTEIN_TELOMERASE_TIER1 = register("protein_telomerase_tier1", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_TELOMERASE_TIER2 = register("protein_telomerase_tier2", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_TELOMERASE_TIER3 = register("protein_telomerase_tier3", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_TELOMERASE_TIER4 = register("protein_telomerase_tier4", Item::new, new Item.Properties().stacksTo(64));

	public static final Item PROTEIN_SPIDER_SILK_TIER1 = register("protein_spider_silk_tier1", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_SPIDER_SILK_TIER2 = register("protein_spider_silk_tier2", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_SPIDER_SILK_TIER3 = register("protein_spider_silk_tier3", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_SPIDER_SILK_TIER4 = register("protein_spider_silk_tier4", Item::new, new Item.Properties().stacksTo(64));

	public static final Item PROTEIN_LUCIFERASE_TIER1 = register("protein_luciferase_tier1", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_LUCIFERASE_TIER2 = register("protein_luciferase_tier2", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_LUCIFERASE_TIER3 = register("protein_luciferase_tier3", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_LUCIFERASE_TIER4 = register("protein_luciferase_tier4", Item::new, new Item.Properties().stacksTo(64));

	public static final Item PROTEIN_PETASE_TIER1 = register("protein_petase_tier1", PetaseRecyclerItem::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_PETASE_TIER2 = register("protein_petase_tier2", PetaseRecyclerItem::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_PETASE_TIER3 = register("protein_petase_tier3", PetaseRecyclerItem::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_PETASE_TIER4 = register("protein_petase_tier4", PetaseRecyclerItem::new, new Item.Properties().stacksTo(64));

	public static final Item PROTEIN_ICE_NUCLEATION_TIER1 = register("protein_ice_nucleation_tier1", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_ICE_NUCLEATION_TIER2 = register("protein_ice_nucleation_tier2", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_ICE_NUCLEATION_TIER3 = register("protein_ice_nucleation_tier3", Item::new, new Item.Properties().stacksTo(64));
	public static final Item PROTEIN_ICE_NUCLEATION_TIER4 = register("protein_ice_nucleation_tier4", Item::new, new Item.Properties().stacksTo(64));

	public static final Item UNKNOWN_BIOLOGICAL_TISSUE = register("unknown_biological_tissue", TissueItem::new, new Item.Properties().stacksTo(64));

	// Biosafety: waste culture from expression; accumulates and causes negative effects
	public static final Item WASTE_CULTURE = register("waste_culture", Item::new, new Item.Properties().stacksTo(64));

	// Guide book: educational content about synthetic biology and mod mechanics
	public static final Item GUIDE_BOOK = register("guide_book", GuideBookItem::new, new Item.Properties().stacksTo(1));

	public static BrothTier getBrothTier(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return null;
		if (stack.is(NUTRIENT_BROTH_GREEN)) return BrothTier.GREEN;
		if (stack.is(NUTRIENT_BROTH_BLUE)) return BrothTier.BLUE;
		if (stack.is(NUTRIENT_BROTH_PURPLE)) return BrothTier.PURPLE;
		if (stack.is(NUTRIENT_BROTH_GOLD)) return BrothTier.GOLD;
		return null;
	}

	private static <T extends Item> T register(String path, Function<Item.Properties, T> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, path));
		T item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	/** 给其他注册类（如方块物品）复用同一套 1.21.2+ 注册流程。 */
	public static <T extends Item> T registerForBlockItem(String path, Function<Item.Properties, T> factory, Item.Properties properties) {
		return register(path, factory, properties);
	}

	/** 在模组初始化时调用，确保本类完成加载、静态注册执行。 */
	public static void initialize() {
		ModDataComponents.initialize();
		IgemMod.LOGGER.info("Mod items ready.");
	}
}
