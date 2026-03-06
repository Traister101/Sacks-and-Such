package mod.traister101.sns.client;

import mod.traister101.sns.common.capability.FoodHolder.CycleDirection;
import mod.traister101.sns.common.capability.SNSCapabilities;
import mod.traister101.sns.common.items.*;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.mixins.client.invoker.AddCustomNbtDataInvoker;
import mod.traister101.sns.network.*;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.SNSUtils.ToggleType;
import mod.traister101.sns.util.handlers.PickBlockHandler;
import top.theillusivec4.curios.api.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.HitResult.Type;

import net.minecraftforge.client.event.InputEvent.*;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ClientForgeEventHandler {

	public static void init(final IEventBus eventBus) {
		eventBus.addListener(ClientForgeEventHandler::onKeyPress);
		eventBus.addListener(ClientForgeEventHandler::onClickInput);
		eventBus.addListener(ClientForgeEventHandler::onMouseScroll);
	}

	public static void onKeyPress(final Key event) {
		// Sanity check
		final var minecraft = Minecraft.getInstance();
		final var player = minecraft.player;
		if (player == null) return;

		if (SNSKeybinds.OPEN_ITEM_CONTAINER.consumeClick()) {
			ItemSlotData slotData = null;
			if (SNSUtils.isCuriosPresent()) {
				final var maybeSlotResult = CuriosApi.getCuriosInventory(player)
						.resolve()
						.flatMap(curiosItemHandler -> curiosItemHandler.findFirstCurio(itemStack -> itemStack.getItem() instanceof ContainerItem));
				if (maybeSlotResult.isPresent()) {
					final var slotResult = maybeSlotResult.get();
					final ItemStack itemStack = slotResult.stack();

					final var maybeItemHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();

					if (maybeItemHandler.isPresent()) {
						final SlotContext slotContext = slotResult.slotContext();
						slotData = new CuriosSlotData(slotContext.identifier(), slotContext.index());
					}
				}
			}

			final Inventory inventory = player.getInventory();
			if (slotData == null) for (int slotIndex = inventory.items.size() - 1; slotIndex >= 0; slotIndex--) {
				final ItemStack itemStack = inventory.items.get(slotIndex);

				if (!(itemStack.getItem() instanceof ContainerItem)) continue;

				if (Inventory.isHotbarSlot(slotIndex)) {
					inventory.selected = slotIndex;
					player.connection.send(new ServerboundSetCarriedItemPacket(slotIndex));
					slotData = new HeldSlotData(InteractionHand.MAIN_HAND);
					break;
				}

				slotData = new InventorySlotData(slotIndex);
				break;
			}

			if (slotData != null) {
				SNSPacketHandler.sendToServer(new ServerboundOpenContainerPacket(slotData));
			}
		}

		if (SNSKeybinds.TOGGLE_PICKUP.isDown()) {
			final ItemStack heldStack = player.getMainHandItem();
			final boolean flag = !NBTHelper.isAutoPickup(heldStack);
			SNSUtils.sendTogglePacket(ToggleType.PICKUP, flag);
			player.displayClientMessage(ToggleType.PICKUP.getTooltip(flag), true);
		}
	}

	private static void onClickInput(final InteractionKeyMappingTriggered event) {
		if (!event.isPickBlock()) return;

		// If we should handle pick block (Client)
		if (SNSConfig.COMMON.doPickBlock.get()) {
			final var minecraft = Minecraft.getInstance();
			// Sanity checks
			if (minecraft.player == null) return;
			if (minecraft.hitResult == null) return;
			// In creative so don't handle
			if (minecraft.player.isCreative()) return;

			event.setCanceled(
					vanillaPickBlock(minecraft.hitResult, minecraft.player, minecraft.level, minecraft.gameMode) || PickBlockHandler.onPickBlock(
							minecraft.player, minecraft.hitResult));
		}
	}

	private static void onMouseScroll(final MouseScrollingEvent event) {
		// Sanity checks
		final var player = Minecraft.getInstance().player;
		if (player == null) return;

		final ItemStack mainHandStack = player.getMainHandItem();

		if (!mainHandStack.is(SNSItems.LUNCHBOX.get())) return;

		if (!player.isShiftKeyDown()) return;

		final double scrollDelta = event.getScrollDelta();

		final boolean scrollForwards = scrollDelta < 0;
		final boolean scrollBackwards = scrollDelta > 0;

		final var capability = mainHandStack.getCapability(SNSCapabilities.FOOD_HOLDER);

		if (scrollForwards) {
			capability.ifPresent(foodHolder -> foodHolder.cycleSelected(CycleDirection.FORWARD));
			SNSPacketHandler.sendToServer(new ServerboundPacketCycleSlotPacket(CycleDirection.FORWARD));
		} else if (scrollBackwards) {
			capability.ifPresent(foodHolder -> foodHolder.cycleSelected(CycleDirection.BACKWARD));
			SNSPacketHandler.sendToServer(new ServerboundPacketCycleSlotPacket(CycleDirection.BACKWARD));
		}
		event.setCanceled(true);
	}

	/**
	 * Copy of vanillas client pick block logic for us to call
	 *
	 * @return If the vanilla handling succeeded
	 */
	public static boolean vanillaPickBlock(final HitResult hitResult, final Player player, final Level level, final MultiPlayerGameMode gameMode) {
		final boolean creative = player.getAbilities().instabuild;

		final ItemStack pickedStack = getPickedStack(player, level, hitResult, creative);

		if (pickedStack.isEmpty()) return false;

		final Inventory inventory = player.getInventory();

		if (creative) {
			inventory.setPickedItem(pickedStack);
			gameMode.handleCreativeModeItemAdd(player.getItemInHand(InteractionHand.MAIN_HAND), Inventory.INVENTORY_SIZE + inventory.selected);
			return true;
		}

		final int slotIndex = inventory.findSlotMatchingItem(pickedStack);
		if (slotIndex != Inventory.NOT_FOUND_INDEX) {
			if (Inventory.isHotbarSlot(slotIndex)) {
				inventory.selected = slotIndex;
			} else {
				gameMode.handlePickItem(slotIndex);
			}
			return true;
		}

		return false;
	}

	/**
	 * @return The picked {@link ItemStack}. {@link ItemStack#EMPTY} for when nothing should be added
	 */
	private static ItemStack getPickedStack(final Player player, final Level level, final HitResult hitResult, final boolean creative) {
		final Type type = hitResult.getType();

		if (type == Type.BLOCK) {
			final BlockPos blockpos = ((BlockHitResult) hitResult).getBlockPos();
			final BlockState blockState = level.getBlockState(blockpos);
			if (blockState.isAir()) return ItemStack.EMPTY;

			final ItemStack itemStack = blockState.getCloneItemStack(hitResult, level, blockpos, player);

			if (creative && Screen.hasControlDown() && blockState.hasBlockEntity()) {
				final BlockEntity blockEntity = level.getBlockEntity(blockpos);
				if (blockEntity != null) {
					((AddCustomNbtDataInvoker) Minecraft.getInstance()).invokeAddCustomNbtData(itemStack, blockEntity);
				}
			}

			return itemStack;
		}

		if (type != Type.ENTITY || !creative) return ItemStack.EMPTY;

		final Entity entity = ((EntityHitResult) hitResult).getEntity();

		return entity.getPickedResult(hitResult);
	}
}