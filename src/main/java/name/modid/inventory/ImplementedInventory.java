package name.modid.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A minimal {@link Container} helper backed by a {@link NonNullList}.
 * This mirrors the common Fabric "ImplementedInventory" pattern.
 */
public interface ImplementedInventory extends Container {
	NonNullList<ItemStack> getItems();

	static ImplementedInventory of(NonNullList<ItemStack> items) {
		return new ImplementedInventory() {
			@Override
			public NonNullList<ItemStack> getItems() {
				return items;
			}
		};
	}

	@Override
	default void setChanged() {
	}

	@Override
	default int getContainerSize() {
		return getItems().size();
	}

	@Override
	default boolean isEmpty() {
		for (ItemStack stack : getItems()) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	default ItemStack getItem(int slot) {
		return getItems().get(slot);
	}

	@Override
	default ItemStack removeItem(int slot, int amount) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty()) return ItemStack.EMPTY;

		ItemStack split = stack.split(amount);
		if (!split.isEmpty()) setChanged();
		return split;
	}

	@Override
	default ItemStack removeItemNoUpdate(int slot) {
		ItemStack removed = getItems().get(slot);
		getItems().set(slot, ItemStack.EMPTY);
		return removed;
	}

	@Override
	default void setItem(int slot, ItemStack stack) {
		getItems().set(slot, stack);
		if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}
		setChanged();
	}

	@Override
	default boolean stillValid(Player player) {
		return true;
	}

	@Override
	default void clearContent() {
		getItems().clear();
	}
}

