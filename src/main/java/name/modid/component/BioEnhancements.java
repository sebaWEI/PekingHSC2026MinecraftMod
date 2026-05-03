package name.modid.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BioEnhancements(int myosinTier, int telomeraseTier, int keratinTier, int gfpTier,
                               int spiderSilkTier, int luciferaseTier, int petaseTier, int iceNucleationTier) {
	public static final BioEnhancements EMPTY = new BioEnhancements(0, 0, 0, 0, 0, 0, 0, 0);

	public static final Codec<BioEnhancements> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.optionalFieldOf("myosin_tier", 0).forGetter(BioEnhancements::myosinTier),
		Codec.INT.optionalFieldOf("telomerase_tier", 0).forGetter(BioEnhancements::telomeraseTier),
		Codec.INT.optionalFieldOf("keratin_tier", 0).forGetter(BioEnhancements::keratinTier),
		Codec.INT.optionalFieldOf("gfp_tier", 0).forGetter(BioEnhancements::gfpTier),
		Codec.INT.optionalFieldOf("spider_silk_tier", 0).forGetter(BioEnhancements::spiderSilkTier),
		Codec.INT.optionalFieldOf("luciferase_tier", 0).forGetter(BioEnhancements::luciferaseTier),
		Codec.INT.optionalFieldOf("petase_tier", 0).forGetter(BioEnhancements::petaseTier),
		Codec.INT.optionalFieldOf("ice_nucleation_tier", 0).forGetter(BioEnhancements::iceNucleationTier)
	).apply(instance, BioEnhancements::new));

	public BioEnhancements {
		myosinTier = clampTier(myosinTier);
		telomeraseTier = clampTier(telomeraseTier);
		keratinTier = clampTier(keratinTier);
		gfpTier = clampTier(gfpTier);
		spiderSilkTier = clampTier(spiderSilkTier);
		luciferaseTier = clampTier(luciferaseTier);
		petaseTier = clampTier(petaseTier);
		iceNucleationTier = clampTier(iceNucleationTier);
	}

	public BioEnhancements withMyosinTier(int tier) {
		return new BioEnhancements(Math.max(this.myosinTier, clampTier(tier)), this.telomeraseTier, this.keratinTier, this.gfpTier,
			this.spiderSilkTier, this.luciferaseTier, this.petaseTier, this.iceNucleationTier);
	}

	public BioEnhancements withTelomeraseTier(int tier) {
		return new BioEnhancements(this.myosinTier, Math.max(this.telomeraseTier, clampTier(tier)), this.keratinTier, this.gfpTier,
			this.spiderSilkTier, this.luciferaseTier, this.petaseTier, this.iceNucleationTier);
	}

	public BioEnhancements withKeratinTier(int tier) {
		return new BioEnhancements(this.myosinTier, this.telomeraseTier, Math.max(this.keratinTier, clampTier(tier)), this.gfpTier,
			this.spiderSilkTier, this.luciferaseTier, this.petaseTier, this.iceNucleationTier);
	}

	public BioEnhancements withGfpTier(int tier) {
		return new BioEnhancements(this.myosinTier, this.telomeraseTier, this.keratinTier, Math.max(this.gfpTier, clampTier(tier)),
			this.spiderSilkTier, this.luciferaseTier, this.petaseTier, this.iceNucleationTier);
	}

	public BioEnhancements withSpiderSilkTier(int tier) {
		return new BioEnhancements(this.myosinTier, this.telomeraseTier, this.keratinTier, this.gfpTier,
			Math.max(this.spiderSilkTier, clampTier(tier)), this.luciferaseTier, this.petaseTier, this.iceNucleationTier);
	}

	public BioEnhancements withLuciferaseTier(int tier) {
		return new BioEnhancements(this.myosinTier, this.telomeraseTier, this.keratinTier, this.gfpTier,
			this.spiderSilkTier, Math.max(this.luciferaseTier, clampTier(tier)), this.petaseTier, this.iceNucleationTier);
	}

	public BioEnhancements withPetaseTier(int tier) {
		return new BioEnhancements(this.myosinTier, this.telomeraseTier, this.keratinTier, this.gfpTier,
			this.spiderSilkTier, this.luciferaseTier, Math.max(this.petaseTier, clampTier(tier)), this.iceNucleationTier);
	}

	public BioEnhancements withIceNucleationTier(int tier) {
		return new BioEnhancements(this.myosinTier, this.telomeraseTier, this.keratinTier, this.gfpTier,
			this.spiderSilkTier, this.luciferaseTier, this.petaseTier, Math.max(this.iceNucleationTier, clampTier(tier)));
	}

	private static int clampTier(int tier) {
		if (tier < 0) return 0;
		if (tier > 4) return 4;
		return tier;
	}
}

