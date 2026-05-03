package name.modid.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record InsertedRegulatoryPart(PartCategory category, RarityTier rarity) {
	public static final Codec<InsertedRegulatoryPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.xmap(PartCategory::valueOf, PartCategory::name).fieldOf("category").forGetter(InsertedRegulatoryPart::category),
		Codec.STRING.xmap(RarityTier::valueOf, RarityTier::name).fieldOf("rarity").forGetter(InsertedRegulatoryPart::rarity)
	).apply(instance, InsertedRegulatoryPart::new));
}

