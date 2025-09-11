package mod.traister101.sns.util;

import com.google.common.collect.Multimap;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.compat.curios.CuriosUtils;
import mod.traister101.sns.network.*;
import mod.traister101.sns.util.items.*;
import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

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

	public static Stream<IItemHandler> curiosAndInventoryStream(final LivingEntity entity) {
		return StreamSupport.stream(curiosAndInventory(entity).spliterator(), false);
	}

	public static Iterable<IItemHandler> curiosAndInventory(final LivingEntity entity) {
		final var inventory = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (isCuriosPresent()) {
			return Stream.of(inventory, CuriosUtils.getEquippedCurios(entity)).flatMap(Optional::stream)::iterator;
		}
		return Stream.of(inventory).flatMap(Optional::stream)::iterator;
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
		for (final var handlerSlot : ItemSlot.iterable(itemHandler)) {
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
		return ItemSlot.reverseStream(itemHandler)
				.filter(ItemSlot.contentsMatch(handlerContentsPredicate.and(Predicate.not(ItemStack::isEmpty))))
				.findFirst();
	}

	public static <T> Stream<T> streamOf(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * @param tooltip The tooltip
	 * @param attributeModifiers The attribute modifiers
	 */
	public static void attributeTooltips(final List<Component> tooltip, final Multimap<Attribute, AttributeModifier> attributeModifiers) {
		for (final var entry : attributeModifiers.entries()) {
			final var modifier = entry.getValue();
			final var amount = modifier.getAmount();

			final double displayAmount;
			if (modifier.getOperation() != Operation.MULTIPLY_BASE && modifier.getOperation() != Operation.MULTIPLY_TOTAL) {
				if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
					displayAmount = amount * 10;
				} else {
					displayAmount = amount;
				}
			} else {
				displayAmount = amount * 100;
			}

			if (amount > 0) {
				tooltip.add(Component.translatable("attribute.modifier.plus." + modifier.getOperation().toValue(),
								ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount), Component.translatable(entry.getKey().getDescriptionId()))
						.withStyle(ChatFormatting.BLUE));
			} else if (amount < 0) {
				tooltip.add(Component.translatable("attribute.modifier.take." + modifier.getOperation().toValue(),
								ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount * -1), Component.translatable(entry.getKey().getDescriptionId()))
						.withStyle(ChatFormatting.RED));
			}
		}
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