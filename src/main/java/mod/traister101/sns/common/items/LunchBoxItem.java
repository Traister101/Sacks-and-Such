package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.menu.SNSMenus;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.ItemSlotData.HeldSlotData;
import net.dries007.tfc.common.capabilities.food.FoodCapability;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class LunchBoxItem extends ContainerItem {

	public static final String SELECTED_SLOT_TOOLTIP = SacksNSuch.MODID + ".tooltip.lunchbox.selected_slot";

	public LunchBoxItem(final Properties properties, final ContainerType type) {
		super(properties, type);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);

		if (!player.isShiftKeyDown()) {
			return heldStack.getCapability(SNSCapabilities.FOOD_HOLDER).map(foodHolder -> {
				final ItemStack targetFood = foodHolder.getSelectedStack();
				if (targetFood.isEmpty()) return InteractionResultHolder.pass(heldStack);
				final FoodProperties targetFoodProperties = targetFood.getFoodProperties(player);
				if (!targetFood.isEmpty() && !player.getCooldowns().isOnCooldown(targetFood.getItem()) && player.canEat(
						(targetFood.isEdible() && targetFoodProperties != null && targetFoodProperties.canAlwaysEat()))) {
					player.startUsingItem(hand);
					return InteractionResultHolder.consume(heldStack);
				}
				return InteractionResultHolder.pass(heldStack);
			}).orElseGet(() -> InteractionResultHolder.pass(heldStack));
		}

		if (!level.isClientSide) {
			if (player.isShiftKeyDown()) {
				SNSMenus.CONTAINER_ITEM_MENU_PROVIDER.openMenu(player, new HeldSlotData(hand));
				return InteractionResultHolder.consume(heldStack);
			}
		}

		return InteractionResultHolder.pass(heldStack);
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(SELECTED_SLOT_TOOLTIP,
					SNSUtils.intComponent(itemStack.getCapability(SNSCapabilities.FOOD_HOLDER).map(FoodHolder::getSelectedSlot).orElse(0) + 1)
							.withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
			super.appendHoverText(itemStack, level, tooltip, flagIn);
			return;
		}

		super.appendHoverText(itemStack, level, tooltip, flagIn);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(final ItemStack itemStack) {
		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().flatMap(handler -> {
			final int width, height;
			switch (handler.getSlots()) {
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
					width = (int) Math.ceil((double) handler.getSlots() / 9);
					height = handler.getSlots() / width;
				}
			}

			return itemStack.getCapability(SNSCapabilities.FOOD_HOLDER)
					.map(FoodHolder::getSelectedSlot)
					.map(selectedSlot -> LunchboxTooltip.getTooltipImage(handler, width, height, selectedSlot));
		}).orElse(super.getTooltipImage(itemStack));
	}

	@Override
	public ItemStack finishUsingItem(final ItemStack itemStack, final Level level, final LivingEntity livingEntity) {
		return itemStack.getCapability(SNSCapabilities.FOOD_HOLDER)
				.map(itemHandler -> itemHandler.consumeSelected(itemStack, level, livingEntity))
				.orElse(itemStack);
	}

	@Override
	public UseAnim getUseAnimation(final ItemStack itemStack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(final ItemStack itemStack) {
		return itemStack.getCapability(SNSCapabilities.FOOD_HOLDER).map(foodHolder -> foodHolder.getSelectedStack().getUseDuration()).orElse(32);
	}

	@Nullable
	@Override
	public FoodProperties getFoodProperties(final ItemStack itemStack, final @Nullable LivingEntity entity) {
		final var capability = itemStack.getCapability(SNSCapabilities.FOOD_HOLDER).resolve();
		return capability.map(foodHolder -> foodHolder.getSelectedFoodProperties(entity)).orElse(null);
	}

	@Getter
	public static class LunchboxHandler extends ContainerItemHandler implements FoodHolder {

		public static final String SELECTED_SLOT_KEY = "selectedSlot";
		private int selectedSlot = 0;

		public LunchboxHandler(final ContainerType type, final ItemStack owner) {
			super(type, owner);
		}

		@Override
		public CompoundTag serializeNBT() {
			final CompoundTag compoundTag = super.serializeNBT();
			compoundTag.putInt(SELECTED_SLOT_KEY, selectedSlot);
			return compoundTag;
		}

		@Override
		public void deserializeNBT(final CompoundTag compoundTag) {
			super.deserializeNBT(compoundTag);
			selectedSlot = compoundTag.getInt(SELECTED_SLOT_KEY);
		}

		@Override
		public ItemStack insertItem(final int slotIndex, final ItemStack insertStack, final boolean simulate) {
			final ItemStack insert = insertStack.copy();
			FoodCapability.applyTrait(insert, LunchboxFoodTrait.LUNCHBOX);
			final ItemStack remainder = super.insertItem(slotIndex, insert, simulate);
			FoodCapability.removeTrait(remainder, LunchboxFoodTrait.LUNCHBOX);
			return remainder;
		}

		@Override
		public void setStackInSlot(final int slotIndex, final ItemStack itemStack) {
			FoodCapability.applyTrait(itemStack, LunchboxFoodTrait.LUNCHBOX);
			super.setStackInSlot(slotIndex, itemStack);
		}

		@Override
		public ItemStack extractItem(final int slotIndex, final int amount, final boolean simulate) {
			final ItemStack extractItem = super.extractItem(slotIndex, amount, simulate);
			FoodCapability.removeTrait(extractItem, LunchboxFoodTrait.LUNCHBOX);
			return extractItem;
		}

		@Override
		public ItemStack getSelectedStack() {
			return getStackInSlot(getSelectedSlot());
		}

		@Override
		public void cycleSelected(final CycleDirection cycleDirection) {
			int nextSelection = selectedSlot + switch (cycleDirection) {
				case FORWARD -> 1;
				case BACKWARD -> -1;
			};

			{ // Wrap to within slot bounds
				if (nextSelection >= getSlots()) {
					nextSelection -= getSlots();
				}

				if (nextSelection < 0) {
					nextSelection += getSlots();
				}
			}

			selectedSlot = nextSelection;
			assert selectedSlot >= 0 && selectedSlot < getSlots() : String.format("Selected Slot: %s must be a valid slot index in range [0,%s)",
					selectedSlot, getSlots());
		}

		@Override
		public ItemStack consumeSelected(final ItemStack itemStack, final Level level, final LivingEntity livingEntity) {
			// This appears to be the best way to handle TFCs way of handling Dynamic food like bowls
			final var foodRemainder = ForgeEventFactory.onItemUseFinish(livingEntity, getSelectedStack().copy(),
					livingEntity.getUseItemRemainingTicks(), livingEntity.eat(level, getSelectedStack()));

			if (foodRemainder != getSelectedStack()) {
				if (livingEntity instanceof final Player player) {
					ItemHandlerHelper.giveItemToPlayer(player, foodRemainder);
				} else {
					if (!foodRemainder.isEmpty() && !level.isClientSide) {
						final ItemEntity itemEntity = new ItemEntity(level, livingEntity.getX(), livingEntity.getY() + 0.5, livingEntity.getZ(),
								foodRemainder);
						itemEntity.setPickUpDelay(40);
						itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));

						level.addFreshEntity(itemEntity);
					}
				}
			}

			while (getSelectedStack().isEmpty() && getSelectedSlot() != 0) {
				cycleSelected(CycleDirection.BACKWARD);
			}
			return itemStack;
		}
	}
}