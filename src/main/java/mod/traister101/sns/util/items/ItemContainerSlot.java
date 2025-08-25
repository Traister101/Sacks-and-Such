package mod.traister101.sns.util.items;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.ItemHandlerHelper;

import lombok.*;
import org.jetbrains.annotations.Range;

@ToString
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class ItemContainerSlot implements ItemSlot {

	@Range(from = 0, to = Integer.MAX_VALUE)
	final int slotIndex;
	final Container container;

	public static ItemContainerSlot of(@Range(from = 0, to = Integer.MAX_VALUE) final int slotIndex, final @NonNull Container container) {
		//noinspection ConstantValue
		if (slotIndex < 0 || slotIndex >= container.getContainerSize())
			throw new RuntimeException("Slot " + slotIndex + " not in valid range - [0," + container.getContainerSize() + ")");
		return new ItemContainerSlot(slotIndex, container);
	}

	@Override
	public ItemStack getStack() {
		return container.getItem(slotIndex);
	}

	@Override
	public ItemStack insertItem(final ItemStack insertStack, final boolean simulate) {
		if (insertStack.isEmpty()) return ItemStack.EMPTY;

		final var currentStack = container.getItem(slotIndex);

		if (!currentStack.isEmpty()) {
			if (currentStack.getCount() >= Math.min(currentStack.getMaxStackSize(), container.getMaxStackSize())) return insertStack;

			if (!ItemHandlerHelper.canItemStacksStack(insertStack, currentStack)) return insertStack;

			if (!container.canPlaceItem(slotIndex, insertStack)) return insertStack;

			final int insertCount = Math.min(insertStack.getMaxStackSize(), container.getMaxStackSize()) - currentStack.getCount();

			if (insertStack.getCount() <= insertCount) {
				if (simulate) return ItemStack.EMPTY;

				container.setItem(slotIndex, insertStack.copyWithCount(insertStack.getCount() + currentStack.getCount()));
				container.setChanged();
				return ItemStack.EMPTY;
			}
			if (simulate) return insertStack.copyWithCount(insertStack.getCount() - insertCount);

			final var stack = insertStack.copy();
			final ItemStack insert = stack.split(insertCount);
			insert.grow(currentStack.getCount());
			container.setItem(slotIndex, insert);
			container.setChanged();
			return stack;
		}

		if (!container.canPlaceItem(slotIndex, insertStack)) return insertStack;

		final int insertCount = Math.min(insertStack.getMaxStackSize(), container.getMaxStackSize());
		if (insertCount < insertStack.getCount()) {
			final var stack = insertStack.copy();
			if (!simulate) {
				container.setItem(slotIndex, stack.split(insertCount));
				container.setChanged();
			} else {
				stack.shrink(insertCount);
			}
			return stack;
		} else {
			if (!simulate) {
				container.setItem(slotIndex, insertStack);
				container.setChanged();
			}
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack extractItem(final int amount, final boolean simulate) {
		if (amount == 0) return ItemStack.EMPTY;

		final var currentStack = container.getItem(slotIndex);

		if (currentStack.isEmpty()) return ItemStack.EMPTY;

		if (simulate) {
			if (currentStack.getCount() < amount) return currentStack.copy();

			return currentStack.copyWithCount(amount);
		}

		final int extractCount = Math.min(currentStack.getCount(), amount);

		final var extractedStack = container.removeItem(slotIndex, extractCount);
		container.setChanged();
		return extractedStack;
	}

	@Override
	public int slotLimit() {
		return container.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(final ItemStack stack) {
		return container.canPlaceItem(slotIndex, stack);
	}
}