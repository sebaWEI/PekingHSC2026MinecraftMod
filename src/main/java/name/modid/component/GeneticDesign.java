package name.modid.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.modid.item.GeneticPartDefinition;

import java.util.ArrayList;
import java.util.List;

public record GeneticDesign(String cdsId, List<InsertedRegulatoryPart> regulatoryParts, int expressionMultiplier) {
	public static final int BASE_MULTIPLIER = 1;
	public static final int MAX_MULTIPLIER = 81;

	public static final GeneticDesign EMPTY = new GeneticDesign("", List.of(), BASE_MULTIPLIER);

	public static final Codec<GeneticDesign> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.optionalFieldOf("cds_id", "").forGetter(GeneticDesign::cdsId),
		InsertedRegulatoryPart.CODEC.listOf().optionalFieldOf("regulatory_parts", List.of()).forGetter(GeneticDesign::regulatoryParts),
		Codec.INT.optionalFieldOf("expression_multiplier", BASE_MULTIPLIER).forGetter(GeneticDesign::expressionMultiplier)
	).apply(instance, GeneticDesign::new));

	public GeneticDesign {
		regulatoryParts = List.copyOf(regulatoryParts);
		expressionMultiplier = Math.min(MAX_MULTIPLIER, Math.max(BASE_MULTIPLIER, expressionMultiplier));
	}

	public boolean hasCds() {
		return !this.cdsId.isBlank();
	}

	public ProteinTier getProteinTier() {
		return ProteinTier.fromAdditiveMultiplier(this.expressionMultiplier);
	}

	public int countCategory(PartCategory category) {
		int count = 0;
		for (InsertedRegulatoryPart part : this.regulatoryParts) {
			if (part.category() == category) count++;
		}
		return count;
	}

	public boolean canInsert(GeneticPartDefinition part) {
		if (part == null) return false;

		if (part.isCds()) {
			return !this.hasCds();
		}

		if (part.category() == null || part.rarity() == null) return false;

		return switch (part.category()) {
			case PROMOTER, UTR3, UTR5, ENHANCER -> countCategory(part.category()) < 1;
			case SINEB2 -> countCategory(PartCategory.SINEB2) < 1;
		};
	}

	public GeneticDesign withInsertedPart(GeneticPartDefinition part) {
		if (!canInsert(part)) return this;

		String nextCds = this.cdsId;
		if (part.isCds()) {
			nextCds = part.productId();
			return new GeneticDesign(nextCds, this.regulatoryParts, this.expressionMultiplier);
		}

		ArrayList<InsertedRegulatoryPart> nextParts = new ArrayList<>(this.regulatoryParts);
		nextParts.add(new InsertedRegulatoryPart(part.category(), part.rarity()));
		int nextMultiplier = Math.min(MAX_MULTIPLIER, this.expressionMultiplier + part.rarity().additiveValue());
		return new GeneticDesign(nextCds, nextParts, nextMultiplier);
	}
}
