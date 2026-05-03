package name.modid.item;

import name.modid.component.ModDataComponents;
import name.modid.component.PartCategory;
import name.modid.component.RarityTier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

/**
 * Unknown biological tissue that carries a hidden "genetic identity" seed.
 * The seed is lazily generated the first time the item enters any inventory,
 * ensuring each tissue has a fixed, deterministic discovery result.
 * <p>
 * Schrödinger's tissue — it collapses to a specific genetic identity
 * the moment it's observed (enters inventory), and stays that way forever.
 */
public class TissueItem extends Item {

	public TissueItem(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (stack.has(ModDataComponents.TISSUE_IDENTITY)) return;

		// First time this tissue enters an inventory — give it a fixed identity
		Random rng = new Random(level.getGameTime() ^ entity.getId() ^ System.nanoTime());
		int w = rng.nextInt(10000);
		RarityTier rarity;
		if (w < 200) {
			rarity = RarityTier.GOLD;
		} else if (w < 1000) {
			rarity = RarityTier.PURPLE;
		} else if (w < 3500) {
			rarity = RarityTier.BLUE;
		} else {
			rarity = RarityTier.GREEN;
		}
		int catIdx = rng.nextInt(PartCategory.values().length);
		int seed = rarity.ordinal() * 10 + catIdx;
		stack.set(ModDataComponents.TISSUE_IDENTITY, seed);
	}
}
