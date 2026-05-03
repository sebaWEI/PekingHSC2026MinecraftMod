package name.modid.component;

public enum RarityTier {
	GREEN(2),
	BLUE(4),
	PURPLE(6),
	GOLD(10);

	private final int additiveValue;

	RarityTier(int additiveValue) {
		this.additiveValue = additiveValue;
	}

	public int additiveValue() {
		return additiveValue;
	}
}

