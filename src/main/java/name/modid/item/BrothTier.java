package name.modid.item;

import name.modid.component.ProteinTier;

public enum BrothTier {
	GREEN,
	BLUE,
	PURPLE,
	GOLD;

	public boolean allows(ProteinTier tier) {
		return switch (this) {
			case GREEN -> tier == ProteinTier.TIER_1;
			case BLUE -> tier == ProteinTier.TIER_1 || tier == ProteinTier.TIER_2;
			case PURPLE -> tier != ProteinTier.TIER_4;
			case GOLD -> true;
		};
	}
}

