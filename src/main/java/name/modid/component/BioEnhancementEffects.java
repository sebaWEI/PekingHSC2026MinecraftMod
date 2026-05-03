package name.modid.component;

import name.modid.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * Tick-based effects for bio-enhanced equipment and biosafety mechanics.
 * Registered in IgemMod.onInitialize().
 */
public class BioEnhancementEffects {

	private static final int WASTE_THRESHOLD = 10;
	private static final int WASTE_CHECK_RADIUS = 32;

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 0) return;

			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tickLuciferase(player);
				tickWasteCulture(player);
			}
		});
	}

	/**
	 * Luciferase-enhanced helmet grants Night Vision.
	 */
	private static void tickLuciferase(ServerPlayer player) {
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		if (helmet.isEmpty()) return;

		BioEnhancements enh = helmet.getOrDefault(ModDataComponents.BIO_ENHANCEMENTS, BioEnhancements.EMPTY);
		int tier = enh.luciferaseTier();
		if (tier <= 0) return;

		int duration = switch (tier) {
			case 4 -> 24000;
			case 3 -> 1200;
			case 2 -> 600;
			default -> 300;
		};

		MobEffectInstance current = player.getEffect(MobEffects.NIGHT_VISION);
		if (current == null || current.getDuration() < 200) {
			player.addEffect(new MobEffectInstance(
				MobEffects.NIGHT_VISION, duration, 0,
				false, false, true
			));
		}
	}

	/**
	 * Biosafety: when >=10 waste culture items exist on the ground near a player,
	 * apply poison (lab contamination).
	 */
	private static void tickWasteCulture(ServerPlayer player) {
		ServerLevel level = (ServerLevel) player.level();
		AABB searchBox = player.getBoundingBox().inflate(WASTE_CHECK_RADIUS);

		int wasteCount = 0;
		for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, searchBox)) {
			if (itemEntity.getItem().is(ModItems.WASTE_CULTURE)) {
				wasteCount += itemEntity.getItem().getCount();
			}
		}

		if (wasteCount >= WASTE_THRESHOLD) {
			int amplifier = Math.min(2, (wasteCount - WASTE_THRESHOLD) / 10);
			player.addEffect(new MobEffectInstance(
				MobEffects.POISON, 60, amplifier,
				false, true, true
			));
			if (wasteCount >= WASTE_THRESHOLD * 3) {
				player.addEffect(new MobEffectInstance(
					MobEffects.NAUSEA, 100, 0,
					false, true, true
				));
			}
		}
	}
}
