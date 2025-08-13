package mod.traister101.sns.util.items;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import lombok.*;
import org.jetbrains.annotations.*;

@ToString
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class ItemHandlerSlot implements ItemSlot {

	@Range(from = 0, to = Integer.MAX_VALUE)
	final int slotIndex;
	final IItemHandler itemHandler;

	public static ItemHandlerSlot of(@Range(from = 0, to = Integer.MAX_VALUE) final int slotIndex, final @NonNull IItemHandler itemHandler) {
		//noinspection ConstantValue
		if (slotIndex < 0 || slotIndex >= itemHandler.getSlots())
			throw new RuntimeException("Slot " + slotIndex + " not in valid range - [0," + itemHandler.getSlots() + ")");
		return new ItemHandlerSlot(slotIndex, itemHandler);
	}

	@Override
	public @NotNull ItemStack getStack() {
		return itemHandler.getStackInSlot(slotIndex);
	}

	@Override
	public ItemStack insertItem(final ItemStack insertStack, final boolean simulate) {
		return itemHandler.insertItem(slotIndex, insertStack, simulate);
	}

	@Override
	public ItemStack extractItem(final int amount, final boolean simulate) {
		return itemHandler.extractItem(slotIndex, amount, simulate);
	}

	@Override
	public boolean isItemValid(final ItemStack stack) {
		return itemHandler.isItemValid(slotIndex, stack);
	}
}