package mod.traister101.sns.util.items;

import com.google.common.collect.AbstractIterator;
import mod.traister101.sns.util.SNSUtils;

import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import org.jetbrains.annotations.Nullable;
import java.util.function.*;
import java.util.stream.Stream;

public sealed interface ItemSlot permits ItemContainerSlot, ItemHandlerSlot {

	/**
	 * @param item The item to test the contents against
	 */
	static Predicate<ItemSlot> contains(final Item item) {
		return contentsMatch(stack -> stack.is(item));
	}

	/**
	 * @param tag The tag to test the contents against
	 */
	static Predicate<ItemSlot> contains(final TagKey<Item> tag) {
		return contentsMatch(stack -> stack.is(tag));
	}

	/**
	 * @param itemStackPredicate An ItemStack predicate to test the contents against
	 */
	static Predicate<ItemSlot> contentsMatch(final Predicate<ItemStack> itemStackPredicate) {
		return slot -> itemStackPredicate.test(slot.getStack());
	}

	/**
	 * @param capability The capability to extract
	 */
	static <T> Function<ItemSlot, LazyOptional<T>> extractCapability(final Capability<T> capability) {
		return slot -> slot.getStack().getCapability(capability);
	}

	/**
	 * Common helper for creating an iterable for some sort of item contents
	 *
	 * @param itemContents The object holding the items
	 * @param sizeGetter The way to get how many slots the backing contents has
	 * @param factory A factory for the Item Slot
	 * @param <C> The contents type
	 * @param <I> The Item Slot type
	 *
	 * @return An iterable which iterates over every slot in the backing item contents
	 */
	private static <C, I extends ItemSlot> Iterable<I> iterable(final C itemContents, final ToIntFunction<C> sizeGetter,
			final Factory<C, I> factory) {
		return () -> new AbstractIterator<>() {
			private final int contentsSize = sizeGetter.applyAsInt(itemContents);
			private int index = 0;

			@Override
			protected @Nullable I computeNext() {
				if (index >= contentsSize) return endOfData();
				assert index >= 0 : "Index: " + index + " not within bounds [0," + contentsSize + ")";
				return factory.create(index++, itemContents);
			}
		};
	}

	/**
	 * Common helper for creating an iterable for some sort of item contents
	 *
	 * @param itemContents The object holding the items
	 * @param sizeGetter The way to get how many slots the backing contents has
	 * @param factory A factory for the Item Slot
	 * @param <C> The contents type
	 * @param <I> The Item Slot type
	 *
	 * @return An iterable which iterates over every slot in the backing item contents in reverse order
	 */
	private static <C, I extends ItemSlot> Iterable<I> reverseIterable(final C itemContents, final ToIntFunction<C> sizeGetter,
			final Factory<C, I> factory) {
		return () -> new AbstractIterator<>() {
			private final int contentsSize = sizeGetter.applyAsInt(itemContents);
			private int index = contentsSize - 1;

			@Override
			protected @Nullable I computeNext() {
				if (index < 0) return endOfData();
				assert index < contentsSize : "Index: " + index + " not within bounds [0," + contentsSize + ")";
				return factory.create(index--, itemContents);
			}
		};
	}

	/**
	 * @param container A Container to iterate through
	 */
	static Iterable<ItemContainerSlot> iterable(final Container container) {
		return ItemSlot.iterable(container, Container::getContainerSize, ItemContainerSlot::new);
	}

	/**
	 * @param container A Container to iterate through
	 */
	static Iterable<ItemContainerSlot> reverseIterable(final Container container) {
		return ItemSlot.reverseIterable(container, Container::getContainerSize, ItemContainerSlot::new);
	}

	/**
	 * @param container A container to stream through
	 */
	static Stream<ItemContainerSlot> stream(final Container container) {
		return SNSUtils.streamOf(iterable(container));
	}

	/**
	 * @param container A container to stream through
	 */
	static Stream<ItemContainerSlot> reverseStream(final Container container) {
		return SNSUtils.streamOf(reverseIterable(container));
	}

	/**
	 * @param itemHandler An Item Handler to iterate through
	 */
	static Iterable<ItemHandlerSlot> iterable(final IItemHandler itemHandler) {
		return ItemSlot.iterable(itemHandler, IItemHandler::getSlots, ItemHandlerSlot::new);
	}

	/**
	 * @param itemHandler An Item Handler to iterate through
	 */
	static Iterable<ItemHandlerSlot> reverseIterable(final IItemHandler itemHandler) {
		return ItemSlot.reverseIterable(itemHandler, IItemHandler::getSlots, ItemHandlerSlot::new);
	}

	/**
	 * @param itemHandler An Item Handler to stream through
	 */
	static Stream<ItemHandlerSlot> stream(final IItemHandler itemHandler) {
		return SNSUtils.streamOf(iterable(itemHandler));
	}

	/**
	 * @param itemHandler An Item Handler to stream through
	 */
	static Stream<ItemHandlerSlot> reverseStream(final IItemHandler itemHandler) {
		return SNSUtils.streamOf(reverseIterable(itemHandler));
	}

	/**
	 * Same contract as {@link IItemHandler#getStackInSlot(int)}
	 **/
	ItemStack getStack();

	/**
	 * Same contract as {@link IItemHandler#insertItem(int, ItemStack, boolean)}
	 **/
	ItemStack insertItem(ItemStack insertStack, boolean simulate);

	/**
	 * Same contract as {@link IItemHandler#extractItem(int, int, boolean)}
	 **/
	ItemStack extractItem(int amount, boolean simulate);

	/**
	 * The maximum stack limit for this slot
	 */
	int slotLimit();

	/**
	 * Same contract as {@link IItemHandler#isItemValid(int, ItemStack)}
	 */
	@SuppressWarnings("unused")
	boolean isItemValid(ItemStack stack);

	interface Factory<C, I extends ItemSlot> {

		I create(int index, C itemHolder);
	}
}