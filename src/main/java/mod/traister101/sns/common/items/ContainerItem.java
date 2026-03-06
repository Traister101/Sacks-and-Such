package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.menu.*;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.ItemSlotData.HeldSlotData;
import mod.traister101.sns.util.SNSUtils.ToggleType;
import mod.traister101.sns.util.items.ItemSlot;
import net.dries007.tfc.common.capabilities.size.*;
import net.dries007.tfc.util.Helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class ContainerItem extends Item implements IItemSize {

	public static final String CONTENTS_TAG = "contents";
	public static final String VOID_SLOTS_TAG = "void_slots";
	public static final String TYPE_NO_PICKUP = SacksNSuch.MODID + ".status.item_container.no_pickup";
	public static final String HOLD_SHIFT_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.shift";
	public static final String PICKUP_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.pickup";
	public static final String VOID_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.void";
	public static final String SLOT_COUNT_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.slot_count";
	public static final String SLOT_CAPACITY_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.slot_capacity";
	public static final String ALLOWED_SIZE_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.allowed_size";
	public static final String INVENTORY_INTERACTION_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.inventory_interaction";

	public final ContainerType type;

	public ContainerItem(final Properties properties, final ContainerType type) {
		super(properties);
		this.type = type;
	}

	private static void serializeToTag(final ItemStack itemStack, final CompoundTag compoundTag, final Capability<?> capability,
			final String tagKey) {
		itemStack.getCapability(capability).ifPresent(handler -> {
			if (handler instanceof final INBTSerializable<?> serializable) {
				compoundTag.put(tagKey, serializable.serializeNBT());
			}
		});
	}

	private static void deserializeFromTag(final ItemStack itemStack, final CompoundTag compoundTag, final Capability<?> capability,
			final String tagKey) {
		final var tag = compoundTag.get(tagKey);
		if (tag == null) return;

		itemStack.getCapability(capability).ifPresent(handler -> {
			if (handler instanceof INBTSerializable<?>) {
				@SuppressWarnings("unchecked") final var serializable = (INBTSerializable<Tag>) handler;
				serializable.deserializeNBT(tag);
			}
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);

		if (level.isClientSide) {
			if (!player.isShiftKeyDown()) return InteractionResultHolder.success(heldStack);

			if (type.doesAutoPickup()) {
				final boolean flag = !NBTHelper.isAutoPickup(heldStack);
				SNSUtils.sendTogglePacket(ToggleType.PICKUP, flag);
				player.displayClientMessage(ToggleType.PICKUP.getTooltip(flag), true);
			} else {
				player.displayClientMessage(Component.translatable(TYPE_NO_PICKUP, this.getName(heldStack)), true);
			}

			return InteractionResultHolder.success(heldStack);
		}

		if (!player.isShiftKeyDown()) {
			SNSMenus.CONTAINER_ITEM_MENU_PROVIDER.openMenu(player, new HeldSlotData(hand));
			return InteractionResultHolder.consume(heldStack);
		}

		return InteractionResultHolder.consume(heldStack);
	}

	@Override
	public boolean overrideStackedOnOther(final ItemStack itemStack, final Slot slot, final ClickAction clickAction, final Player player) {
		if (!type.doesInventoryInteraction()) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableContainerInventoryInteraction.get()) return false;

		final var maybeHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (maybeHandler.isEmpty()) return false;

		// Extract items into the slot
		if (!slot.hasItem()) {
			for (final var handlerSlot : ItemSlot.reverseIterable(maybeHandler.get())) {
				final var simulate = handlerSlot.extractItem(Container.LARGE_MAX_STACK_SIZE, true);
				if (simulate.isEmpty()) continue;

				final ItemStack extracted = handlerSlot.extractItem(Container.LARGE_MAX_STACK_SIZE, false);
				final ItemStack remainder = slot.safeInsert(extracted);

				if (!remainder.isEmpty()) {
					handlerSlot.insertItem(remainder, false);
					player.containerMenu.slotsChanged(slot.container);
					playRemoveOneSound(player);
					return true;
				}
			}
			return false;
		}

		final var slotStack = slot.getItem();
		// We have to simulate the insertion to account for crafting result slots
		final var simulate = ItemHandlerHelper.insertItemStacked(maybeHandler.get(), slotStack, true);
		final var extracted = slot.safeTake(slotStack.getCount(), slotStack.getCount() - simulate.getCount(), player);
		if (extracted.isEmpty()) return false;

		ItemHandlerHelper.insertItemStacked(maybeHandler.get(), extracted, false);
		player.containerMenu.slotsChanged(slot.container);
		playInsertSound(player);
		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(final ItemStack itemStack, final ItemStack carriedStack, final Slot slot, final ClickAction clickAction,
			final Player player, final SlotAccess carriedSlot) {
		if (!type.doesInventoryInteraction()) return false;
		if (!slot.allowModification(player)) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableContainerInventoryInteraction.get()) return false;

		final var maybeHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (maybeHandler.isEmpty()) return false;

		if (carriedStack.isEmpty()) {
			for (final var handlerSlot : ItemSlot.reverseIterable(maybeHandler.get())) {
				final var current = handlerSlot.getStack();
				if (current.isEmpty()) continue;

				carriedSlot.set(handlerSlot.extractItem(Container.LARGE_MAX_STACK_SIZE, false));
				player.containerMenu.slotsChanged(slot.container);
				playRemoveOneSound(player);
				return true;
			}
			return false;
		}

		final var remainder = ItemHandlerHelper.insertItemStacked(maybeHandler.get(), carriedStack, false);
		if (remainder.getCount() == carriedStack.getCount()) return false;

		carriedSlot.set(remainder);
		player.containerMenu.slotsChanged(slot.container);
		playInsertSound(player);
		return true;
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flagIn) {
		if (!Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(HOLD_SHIFT_TOOLTIP).withStyle(ChatFormatting.GRAY));
			return;
		}

		tooltip.add(Component.translatable(SLOT_COUNT_TOOLTIP, Component.literal(String.valueOf(type.slotCount())).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY));
		tooltip.add(
				Component.translatable(SLOT_CAPACITY_TOOLTIP, Component.literal(String.valueOf(type.slotCapacity())).withStyle(ChatFormatting.WHITE))
						.withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(ALLOWED_SIZE_TOOLTIP, Helpers.translateEnum(type.allowedSize()).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY));

		if (type.doesAutoPickup()) {
			tooltip.add(
					Component.translatable(PICKUP_TOOLTIP, SNSUtils.toggleTooltip(NBTHelper.isAutoPickup(itemStack))).withStyle(ChatFormatting.GRAY));
		}

		if (type.doesVoiding()) {
			tooltip.add(Component.translatable(VOID_TOOLTIP,
							SNSUtils.toggleTooltip(itemStack.getCapability(SNSCapabilities.ITEM_VOIDER).map(ItemVoider::isVoidingEnabled).orElse(false)))
					.withStyle(ChatFormatting.GRAY));
		}

		if (type.doesInventoryInteraction()) {
			tooltip.add(Component.translatable(INVENTORY_INTERACTION_TOOLTIP, SNSUtils.toggleTooltip(true)).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(final ItemStack itemStack) {
		if (!SNSConfig.CLIENT.displayItemContentsAsImages.get()) return super.getTooltipImage(itemStack);

		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
			final int width, height;
			final int slotCount = handler.getSlots();
			switch (slotCount) {
				case 1 -> width = height = 1;
				case 4 -> width = height = 2;
				case 8 -> {
					width = 4;
					height = 2;
				}
				case 18 -> {
					width = 9;
					height = 2;
				}
				default -> {
					// We want to round up, integer math rounds down
					width = (int) Math.ceil((double) slotCount / 9);
					height = slotCount / width;
				}
			}
			return Helpers.getTooltipImage(handler, width, height, 0, slotCount - 1);
		}).orElse(super.getTooltipImage(itemStack));
	}

	@Override
	public boolean isFoil(final ItemStack itemStack) {
		return SNSConfig.CLIENT.voidGlint.get() ?
				itemStack.getCapability(SNSCapabilities.ITEM_VOIDER).map(ItemVoider::isVoidingEnabled).orElse(false) :
				NBTHelper.isAutoPickup(itemStack);
	}

	@Nullable
	@Override
	public CompoundTag getShareTag(final ItemStack itemStack) {
		final CompoundTag shareTag = super.getShareTag(itemStack);
		final CompoundTag compoundTag = shareTag == null ? new CompoundTag() : shareTag;

		serializeToTag(itemStack, compoundTag, ForgeCapabilities.ITEM_HANDLER, CONTENTS_TAG);
		serializeToTag(itemStack, compoundTag, SNSCapabilities.ITEM_VOIDER, VOID_SLOTS_TAG);

		return compoundTag;
	}

	@Override
	public void readShareTag(final ItemStack itemStack, @Nullable final CompoundTag compoundTag) {
		if (compoundTag == null) {
			super.readShareTag(itemStack, null);
			return;
		}
		{
			final var tag = compoundTag.copy();
			tag.remove(CONTENTS_TAG);
			tag.remove(VOID_SLOTS_TAG);
			super.readShareTag(itemStack, tag);
		}

		deserializeFromTag(itemStack, compoundTag, ForgeCapabilities.ITEM_HANDLER, CONTENTS_TAG);
		deserializeFromTag(itemStack, compoundTag, SNSCapabilities.ITEM_VOIDER, VOID_SLOTS_TAG);
	}

	@Override
	public final ICapabilityProvider initCapabilities(final ItemStack itemStack, @Nullable final CompoundTag nbt) {
		return type.initCapabilities(itemStack, nbt);
	}

	private void playRemoveOneSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private void playInsertSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	@Override
	public Size getSize(final ItemStack itemStack) {
		return type.size(itemStack);
	}

	@Override
	public Weight getWeight(final ItemStack itemStack) {
		return type.weight(itemStack);
	}

	@Override
	public int getDefaultStackSize(final ItemStack itemStack) {
		return 1;
	}
}