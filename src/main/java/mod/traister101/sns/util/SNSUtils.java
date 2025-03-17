package mod.traister101.sns.util;

import com.google.common.collect.AbstractIterator;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.network.*;
import mod.traister101.sns.util.items.ItemHandlerSlot;
import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.*;

@Slf4j
public final class SNSUtils {

	public static final String ENABLED = SacksNSuch.MODID + ".enabled";
	public static final String DISABLED = SacksNSuch.MODID + ".disabled";

	public static final boolean CURIOS_LOADED = ModList.get().isLoaded(CuriosApi.MODID);

	public static void sendTogglePacket(final ToggleType toggleType, final boolean flag) {
		SNSPacketHandler.sendToServer(new ServerboundTogglePacket(flag, toggleType));
	}

	public static MutableComponent toggleTooltip(final boolean flag) {
		return flag ? Component.translatable(ENABLED).withStyle(ChatFormatting.GREEN) :
				Component.translatable(DISABLED).withStyle(ChatFormatting.RED);
	}

	public static ResourceLocation modLocation(final String name) {
		return new ResourceLocation(SacksNSuch.MODID, name);
	}

	public static MutableComponent intComponent(final int i) {
		return Component.literal(String.valueOf(i));
	}

	public static boolean isCuriosPresent() {
		return CURIOS_LOADED;
	}

	/**
	 * @param itemHandler Some sort of ItemHandler to iterate through
	 */
	public static Iterable<ItemHandlerSlot> itemHandlerSlotIterator(final IItemHandler itemHandler) {
		return () -> new AbstractIterator<>() {
			private int index = 0;

			@Override
			protected @Nullable ItemHandlerSlot computeNext() {
				if (index >= itemHandler.getSlots()) return endOfData();
				assert index >= 0 && index < itemHandler.getSlots() : "Index: " + index + " not within bounds [0," + itemHandler.getSlots() + ")";
				return new ItemHandlerSlot(index++, itemHandler);
			}
		};
	}

	public static Iterable<ItemHandlerSlot> itemHandlerSlotReverseIterator(final IItemHandler itemHandler) {
		return () -> new AbstractIterator<>() {
			private int index = itemHandler.getSlots() - 1;

			@Override
			protected @Nullable ItemHandlerSlot computeNext() {
				if (index < 0) return endOfData();
				assert index < itemHandler.getSlots() : "Index: " + index + " not within bounds [0," + itemHandler.getSlots() + ")";
				return new ItemHandlerSlot(index--, itemHandler);
			}
		};
	}

	/**
	 * @param itemHandler An ItemHandler to stream through
	 */
	public static Stream<ItemHandlerSlot> itemHandlerSlotStream(final IItemHandler itemHandler) {
		return StreamSupport.stream(itemHandlerSlotIterator(itemHandler).spliterator(), false);
	}

	/**
	 * @param itemHandler An ItemHandler to stream through
	 */
	public static Stream<ItemHandlerSlot> itemHandlerSlotReverseStream(final IItemHandler itemHandler) {
		return StreamSupport.stream(itemHandlerSlotReverseIterator(itemHandler).spliterator(), false);
	}

	/**
	 * Tops off the ItemHandler until the insert stack is fully consumed
	 *
	 * @param itemHandler The {@link IItemHandler} to insert into
	 * @param insertStack The {@link ItemStack} we insert
	 *
	 * @return The remainder
	 */
	public static ItemStack insertItemOnlyStacked(final IItemHandler itemHandler, ItemStack insertStack) {
		for (final var handlerSlot : itemHandlerSlotIterator(itemHandler)) {
			final var currentStack = handlerSlot.getStack();
			// We only add to existing stacks.
			if (currentStack.isEmpty()) continue;
			// Already full
			if (currentStack.getCount() >= currentStack.getMaxStackSize()) continue;
			// Can merge stacks
			if (!ItemStack.isSameItemSameTags(currentStack, insertStack)) continue;

			insertStack = handlerSlot.insertItem(insertStack, false);
			if (insertStack.isEmpty()) return ItemStack.EMPTY;
		}
		return insertStack;
	}

	public static Optional<ItemHandlerSlot> findFirstInHandler(final IItemHandler itemHandler, final Predicate<ItemStack> handlerContentsPredicate) {
		return itemHandlerSlotReverseStream(itemHandler).filter(
				ItemHandlerSlot.contentsMatch(handlerContentsPredicate.and(Predicate.not(ItemStack::isEmpty)))).findFirst();
	}

	/**
	 * An enum for easy and consistent toggle logic
	 */
	public enum ToggleType {
		NONE(0, "", ""),
		PICKUP(1, SacksNSuch.MODID + ".status.sack.auto_pickup", "pickup");

		private static final IntFunction<ToggleType> BY_ID = ByIdMap.continuous(ToggleType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		@Getter
		public final int id;
		public final String langKey;
		public final String tag;

		ToggleType(final int id, final String langKey, final String tag) {
			this.id = id;
			this.langKey = langKey;
			this.tag = tag;
		}

		public static ToggleType byId(final int toggleTypeId) {
			return BY_ID.apply(toggleTypeId);
		}

		public boolean supportsContainerType(final ContainerType containerType) {
			return switch (this) {
				case NONE -> false;
				case PICKUP -> containerType.doesAutoPickup();
			};
		}

		public MutableComponent getTooltip(final boolean flag) {
			return Component.translatable(langKey, toggleTooltip(flag));
		}
	}
}