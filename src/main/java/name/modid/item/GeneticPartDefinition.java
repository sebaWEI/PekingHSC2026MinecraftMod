package name.modid.item;

import name.modid.component.PartCategory;
import name.modid.component.RarityTier;

public record GeneticPartDefinition(boolean cds, PartCategory category, RarityTier rarity, String productId) {
	public boolean isCds() {
		return cds;
	}

	public static GeneticPartDefinition cds(String cdsId) {
		return new GeneticPartDefinition(true, null, null, cdsId);
	}

	public static GeneticPartDefinition regulatory(PartCategory category, RarityTier rarity) {
		return new GeneticPartDefinition(false, category, rarity, "");
	}
}
