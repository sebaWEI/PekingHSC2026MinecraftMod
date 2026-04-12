package name.modid.item;

import name.modid.IgemMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

/**
 * 1.21.2+ 需在 {@link Item.Properties} 上绑定 {@link ResourceKey}，否则运行时报 “Item id not set”。
 * 流程：{@code ResourceKey} → {@link Item.Properties#setId(ResourceKey)} → 构造 {@link Item} → {@link Registry#register}。
 */
public final class ModItems {
	private ModItems() {
	}

	public static final Item CUSTOM_ITEM = register("custom_item", Item::new, new Item.Properties());

	private static <T extends Item> T register(String path, Function<Item.Properties, T> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(IgemMod.MOD_ID, path));
		T item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	/** 在模组初始化时调用，确保本类完成加载、静态注册执行。 */
	public static void initialize() {
		IgemMod.LOGGER.info("Mod items ready (custom_item id ok).");
	}
}
