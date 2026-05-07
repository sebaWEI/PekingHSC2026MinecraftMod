package name.modid;

import name.modid.block.ModBlocks;
import name.modid.block.entity.ModBlockEntities;
import name.modid.component.BioEnhancementEffects;
import name.modid.item.ModItems;
import name.modid.loot.ModLoot;
import name.modid.recipe.ModRecipes;
import name.modid.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IgemMod implements ModInitializer {
	public static final String MOD_ID = "synbio";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModScreenHandlers.initialize();
		ModItems.initialize();
		ModRecipes.initialize();
		ModLoot.initialize();
		BioEnhancementEffects.register();

		// Starter kit: guide book + blank plasmid + all synbio recipes unlocked
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var player = handler.getPlayer();

			// Unlock all synbio recipes
			var keys = server.getRecipeManager().getRecipes()
				.stream()
				.filter(r -> r.id().toString().startsWith("synbio:"))
				.map(r -> r.id())
				.toList();
			player.awardRecipesByKey(keys);

			// Give guide book + blank plasmid if missing
			var inv = player.getInventory();
			boolean hasGuide = false;
			boolean hasPlasmid = false;
			for (int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);
				if (stack.is(ModItems.GUIDE_BOOK)) hasGuide = true;
				if (stack.is(ModItems.BLANK_PLASMID)) hasPlasmid = true;
			}
			if (!hasGuide) {
				if (!inv.add(new ItemStack(ModItems.GUIDE_BOOK))) {
					player.drop(new ItemStack(ModItems.GUIDE_BOOK), false);
				}
			}
			if (!hasPlasmid) {
				if (!inv.add(new ItemStack(ModItems.BLANK_PLASMID))) {
					player.drop(new ItemStack(ModItems.BLANK_PLASMID), false);
				}
			}
		});

		LOGGER.info("SynBio Crafter ready.");
	}
}
