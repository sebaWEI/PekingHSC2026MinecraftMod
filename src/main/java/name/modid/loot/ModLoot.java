package name.modid.loot;

import name.modid.IgemMod;
import name.modid.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

public final class ModLoot {
	private ModLoot() {
	}

	private static final float TISSUE_CHANCE_DEFAULT = 0.25f;
	private static final float TISSUE_CHANCE_MONSTER = 0.34f;
	private static final float CDS_DROP_CHANCE = 0.5f;

	public static void initialize() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			Identifier id = key.identifier();
			if (!"minecraft".equals(id.getNamespace())) return;
			if (!id.getPath().startsWith("entities/")) return;
			if ("entities/player".equals(id.getPath())) return;

			float tissueChance = TISSUE_CHANCE_DEFAULT;
			String entityPath = id.getPath().substring("entities/".length());
			Identifier entityId = Identifier.withDefaultNamespace(entityPath);
			EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(entityId);
			if (entityType != null && entityType.getCategory() == MobCategory.MONSTER) {
				tissueChance = TISSUE_CHANCE_MONSTER;
			}

			LootPool tissuePool = LootPool.lootPool()
				.when(LootItemRandomChanceCondition.randomChance(tissueChance))
				.add(LootItem.lootTableItem(ModItems.UNKNOWN_BIOLOGICAL_TISSUE))
				.build();
			tableBuilder.pool(tissuePool);

			switch (id.getPath()) {
				case "entities/glow_squid" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_GFP));
				case "entities/spider", "entities/cave_spider" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_CDS_SPIDER_SILK));
				case "entities/zombie" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_CDS_KERATIN));
				case "entities/cow", "entities/mooshroom" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_CDS_MYOSIN));
				case "entities/enderman" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_CDS_TELOMERASE));
				case "entities/blaze" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_CDS_LUCIFERASE));
				case "entities/drowned" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_CDS_PETASE));
				case "entities/stray" -> tableBuilder.pool(cdsPool(ModItems.DNA_PART_CDS_ICE_NUCLEATION));
				default -> {
				}
			}
		});

		IgemMod.LOGGER.info("Mod loot ready.");
	}

	private static LootPool cdsPool(net.minecraft.world.item.Item item) {
		return LootPool.lootPool()
			.when(LootItemRandomChanceCondition.randomChance(CDS_DROP_CHANCE))
			.add(LootItem.lootTableItem(item))
			.build();
	}
}
