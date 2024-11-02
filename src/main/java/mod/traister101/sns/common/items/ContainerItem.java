package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.menu.ContainerItemMenu;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.SNSUtils.ToggleType;
import net.dries007.tfc.common.capabilities.size.*;
import net.dries007.tfc.util.Helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

public class ContainerItem extends Item implements IItemSize {

	public static final String CONTENTS_TAG = "contents";
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

	private static SimpleMenuProvider createMenuProvider(final InteractionHand hand, final ItemStack heldStack) {
		final IItemHandler itemHandler = heldStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();

		return new SimpleMenuProvider((windowId, inventory, unused) -> ContainerItemMenu.forHeld(windowId, inventory, itemHandler, hand),
				heldStack.getHoverName());
	}

	protected static void openMenu(final ServerPlayer player, final InteractionHand hand, final ItemStack heldStack) {
		NetworkHooks.openScreen(player, createMenuProvider(hand, heldStack), ContainerItemMenu.writeHeld(hand));
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
			openMenu((ServerPlayer) player, hand, heldStack);
			return InteractionResultHolder.consume(heldStack);
		}

		return InteractionResultHolder.consume(heldStack);
	}

	@Override
	public boolean overrideStackedOnOther(final ItemStack itemStack, final Slot slot, final ClickAction clickAction, final Player player) {
		if (!type.doesInventoryInteraction()) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableContainerInventoryInteraction.get()) return false;

		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
			if (!slot.hasItem()) {
				for (int slotIndex = handler.getSlots() - 1; slotIndex >= 0; slotIndex--) {
					final ItemStack simulate = handler.extractItem(slotIndex, Container.LARGE_MAX_STACK_SIZE, true);
					if (simulate.isEmpty()) continue;

					final ItemStack extracted = handler.extractItem(slotIndex, Container.LARGE_MAX_STACK_SIZE, false);
					final ItemStack leftover = slot.safeInsert(extracted);

					if (!leftover.isEmpty()) continue;

					handler.insertItem(slotIndex, leftover, false);
					player.containerMenu.slotsChanged(slot.container);
					playRemoveOneSound(player);
					return true;
				}
				return false;
			}

			boolean slotsChanged = false;
			final int initalCount = slot.getItem().getCount();

			for (int slotIndex = 0; slotIndex < handler.getSlots(); slotIndex++) {
				final ItemStack remainder = handler.insertItem(slotIndex, slot.getItem(), false);

				if (remainder.getCount() != initalCount || slotsChanged) {
					slotsChanged = true;
					slot.set(remainder);
				}

				if (remainder.isEmpty()) break;
			}

			if (slotsChanged) {
				player.containerMenu.slotsChanged(slot.container);
				playInsertSound(player);
				return true;
			}

			return false;
		}).orElse(false);
	}

	@Override
	public boolean overrideOtherStackedOnMe(final ItemStack itemStack, final ItemStack carriedStack, final Slot slot, final ClickAction clickAction,
			final Player player, final SlotAccess carriedSlot) {
		if (!type.doesInventoryInteraction()) return false;
		if (!slot.allowModification(player)) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableContainerInventoryInteraction.get()) return false;

		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {

			if (carriedStack.isEmpty()) {
				for (int slotIndex = handler.getSlots() - 1; slotIndex >= 0; slotIndex--) {
					final ItemStack current = handler.getStackInSlot(slotIndex);
					if (current.isEmpty()) continue;

					carriedSlot.set(handler.extractItem(slotIndex, Container.LARGE_MAX_STACK_SIZE, false));

					player.containerMenu.slotsChanged(slot.container);
					playRemoveOneSound(player);
					return true;
				}
				return false;
			}

			boolean slotsChanged = false;
			final int initalCount = carriedStack.getCount();

			{
				ItemStack remainder = handler.insertItem(0, carriedStack, false);

				if (remainder.getCount() != initalCount) {
					slotsChanged = true;
					carriedSlot.set(remainder);
				}

				for (int slotIndex = 1; slotIndex < handler.getSlots(); slotIndex++) {

					remainder = handler.insertItem(slotIndex, remainder, false);

					if (remainder.getCount() != initalCount || slotsChanged) {
						slotsChanged = true;
						carriedSlot.set(remainder);
					}

					if (remainder.isEmpty()) break;
				}
			}

			if (!slotsChanged) return false;

			player.containerMenu.slotsChanged(slot.container);
			playInsertSound(player);
			return true;

		}).orElse(false);
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flagIn) {
		if (!Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(HOLD_SHIFT_TOOLTIP).withStyle(ChatFormatting.GRAY));
			return;
		}

		tooltip.add(Component.translatable(SLOT_COUNT_TOOLTIP, Component.literal(String.valueOf(type.getSlotCount())).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(SLOT_CAPACITY_TOOLTIP,
				Component.literal(String.valueOf(type.getSlotCapacity())).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(ALLOWED_SIZE_TOOLTIP, Helpers.translateEnum(type.getAllowedSize()).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY));

		if (type.doesAutoPickup()) {
			tooltip.add(
					Component.translatable(PICKUP_TOOLTIP, SNSUtils.toggleTooltip(NBTHelper.isAutoPickup(itemStack))).withStyle(ChatFormatting.GRAY));
		}

		if (type.doesVoiding()) {
			tooltip.add(Component.translatable(VOID_TOOLTIP, SNSUtils.toggleTooltip(
							itemStack.getCapability(SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER).map(IVoidingItemHandler::isVoidingEnabled).orElse(false)))
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
			final int width, hight;
			final int slotCount = handler.getSlots();
			switch (slotCount) {
				case 1 -> width = hight = 1;
				case 4 -> width = hight = 2;
				case 8 -> {
					width = 4;
					hight = 2;
				}
				case 18 -> {
					width = 9;
					hight = 2;
				}
				default -> {
					// We want to round up, integer math rounds down
					width = (int) Math.ceil((double) slotCount / 9);
					hight = slotCount / width;
				}
			}
			return Helpers.getTooltipImage(handler, width, hight, 0, slotCount - 1);
		}).orElse(super.getTooltipImage(itemStack));
	}

	@Override
	public boolean isFoil(final ItemStack itemStack) {
		return SNSConfig.CLIENT.voidGlint.get() ?
				itemStack.getCapability(SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER).map(IVoidingItemHandler::isVoidingEnabled).orElse(false) :
				NBTHelper.isAutoPickup(itemStack);
	}

	@Nullable
	@Override
	public CompoundTag getShareTag(final ItemStack itemStack) {
		final CompoundTag shareTag = super.getShareTag(itemStack);
		final CompoundTag compoundTag = shareTag == null ? new CompoundTag() : shareTag;

		// Serialize our contents
		itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			if (handler instanceof final ContainerItemHandler containerItemHandler) {
				compoundTag.put(CONTENTS_TAG, containerItemHandler.serializeNBT());
			}
		});

		return compoundTag;
	}

	@Override
	public void readShareTag(final ItemStack itemStack, @Nullable final CompoundTag compoundTag) {
		super.readShareTag(itemStack, compoundTag);

		if (compoundTag == null) return;

		// Deserlialize our contents
		itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			if (handler instanceof final ContainerItemHandler containerItemHandler) {
				containerItemHandler.deserializeNBT(compoundTag.getCompound(CONTENTS_TAG));
			}
		});
	}

	@Override
	public final ICapabilityProvider initCapabilities(final ItemStack itemStack, @Nullable final CompoundTag nbt) {
		return type.getCapabilityProvider(itemStack, nbt);
	}

	private void playRemoveOneSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private void playInsertSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	@Override
	public Size getSize(final ItemStack itemStack) {
		return type.getSize(itemStack);
	}

	@Override
	public Weight getWeight(final ItemStack itemStack) {
		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.map(handler -> handler instanceof final ContainerItemHandler containerItemHandler ?
						containerItemHandler.getWeight() :
						Weight.VERY_HEAVY)
				.orElse(Weight.VERY_HEAVY);
	}

	@Override
	public int getDefaultStackSize(final ItemStack itemStack) {
		return 1;
	}
}