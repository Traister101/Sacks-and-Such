package mod.traister101.sns.util.items;

import net.minecraft.world.item.*;

import net.minecraftforge.items.IItemHandler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.*;
import java.util.function.Predicate;

/**
 * @param slotIndex The slot index
 * @param itemHandler
 */
@Slf4j
public record ItemHandlerSlot(@Range(from = 0, to = Integer.MAX_VALUE) int slotIndex, @NonNull IItemHandler itemHandler) {

	/**
	 * @param item The item to test the contents against
	 */
	public static Predicate<ItemHandlerSlot> contains(final Item item) {
		return slot -> slot.getStack().is(item);
	}

	/**
	 * @param itemStackPredicate An ItemStack predicate to test the contents against
	 */
	public static Predicate<ItemHandlerSlot> contentsMatch(final Predicate<ItemStack> itemStackPredicate) {
		return slot -> itemStackPredicate.test(slot.getStack());
	}

	/**
	 * Same contract as {@link IItemHandler#getStackInSlot(int)}
	 **/
	@NotNull
	public ItemStack getStack() {
		return itemHandler.getStackInSlot(slotIndex);
	}

	/**
	 * Same contract as {@link IItemHandler#insertItem(int, ItemStack, boolean)}
	 **/
	@SuppressWarnings("unused")
	public ItemStack insertItem(final ItemStack insertStack, final boolean simulate) {
		return itemHandler.insertItem(slotIndex, insertStack, simulate);
	}

	/**
	 * Same contract as {@link IItemHandler#extractItem(int, int, boolean)}
	 **/
	public ItemStack extractItem(final int amount, final boolean simulate) {
		return itemHandler.extractItem(slotIndex, amount, simulate);
	}

	/**
	 * Retrieves the maximum stack size allowed to exist in this slot.
	 *
	 * @return The maximum stack size allowed.
	 */
	@SuppressWarnings("unused")
	public int getSlotLimit() {
		return itemHandler.getSlotLimit(slotIndex);
	}

	/**
	 * Same contract as {@link IItemHandler#isItemValid(int, ItemStack)}
	 */
	@SuppressWarnings("unused")
	public boolean isItemValid(final ItemStack stack) {
		return itemHandler.isItemValid(slotIndex, stack);
	}
}