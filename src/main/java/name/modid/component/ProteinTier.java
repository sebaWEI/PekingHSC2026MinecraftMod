package name.modid.component;

/**
 * 由质粒 {@code expression_multiplier}（含基底 1 + 各调节元件稀有度加值）映射蛋白层级。
 * 当前设计下调节位最多 5 个（各 1 + SINEB2×1），全金时理论最大为 {@code 1 + 5×10 = 51}，阈值按此重标定。
 */
public enum ProteinTier {
	TIER_1,
	TIER_2,
	TIER_3,
	TIER_4;

	public static ProteinTier fromAdditiveMultiplier(int multiplier) {
		if (multiplier >= 40) return TIER_4;
		if (multiplier >= 28) return TIER_3;
		if (multiplier >= 16) return TIER_2;
		return TIER_1;
	}
}

