package mod.traister101.sns.util.handlers;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.network.*;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.items.*;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.HitResult.Type;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PickBlockHandler {

	/**
	 * Handles the client pickblock and informs the server of the event
	 *
	 * @param player The player to handle
	 * @param target The target the player is aiming at
	 */
	public static boolean onPickBlock(final Player player, final HitResult target) {
		final Level world = player.level();

		// Only handle pick blocks
		if (target.getType() != Type.BLOCK) return false;

		final ItemStack stackToSelect;
		{
			final BlockPos blockPos = ((BlockHitResult) target).getBlockPos();
			final BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isAir()) return false;

			stackToSelect = blockState.getBlock().getCloneItemStack(blockState, target, world, blockPos, player);
		}

		// Nothing to select
		if (stackToSelect.isEmpty()) return false;

		SNSPacketHandler.sendToServer(new ServerboundPickBlockPacket(stackToSelect));
		return true;
	}

	/**
	 * Our handler for the server side of our pick block. Called from the packet sent from the client
	 *
	 * @param player The player we handle
	 * @param stackToSelect The stack to select
	 */
	public static void handlePickBlock(final ServerPlayer player, final ItemStack stackToSelect) {
		final Inventory inventoryPlayer = player.getInventory();

		final var freeSlot = inventoryPlayer.getFreeSlot();
		// We only bother checking if there's an empty slot, so we don't have to do any annoying stack merging
		if (freeSlot == Inventory.NOT_FOUND_INDEX) return;

		final ItemStack foundStack = findStackInInventory(inventoryPlayer, stackToSelect);

		// Didn't find a matching ItemStack
		if (foundStack.isEmpty()) return;

		inventoryPlayer.setItem(freeSlot, foundStack);

		if (Inventory.isHotbarSlot(freeSlot)) {
			inventoryPlayer.selected = freeSlot;
		} else {
			inventoryPlayer.pickSlot(freeSlot);
		}

		player.connection.send(new ClientboundContainerSetSlotPacket(ClientboundContainerSetSlotPacket.PLAYER_INVENTORY, 0, inventoryPlayer.selected,
				inventoryPlayer.getItem(inventoryPlayer.selected)));
		player.connection.send(new ClientboundContainerSetSlotPacket(ClientboundContainerSetSlotPacket.PLAYER_INVENTORY, 0, freeSlot,
				inventoryPlayer.getItem(freeSlot)));
		player.connection.send(new ClientboundSetCarriedItemPacket(inventoryPlayer.selected));
	}

	/**
	 * Finds and extracts a matching item stack from an Item Container inside the Player Inventory
	 *
	 * @param inventoryPlayer The player inventory to search
	 * @param stackToMatch The item we are looking for
	 *
	 * @return Extracted ItemStack or an empty ItemStack if one could not be found
	 */
	private static ItemStack findStackInInventory(final Inventory inventoryPlayer, final ItemStack stackToMatch) {
		if (SNSUtils.isCuriosPresent()) {
			final var itemStack = CuriosApi.getCuriosInventory(inventoryPlayer.player)
					.map(ICuriosItemHandler::getEquippedCurios)
					.map(equippedCurios -> findStack(equippedCurios, stackToMatch))
					.orElse(ItemStack.EMPTY);
			if (!itemStack.isEmpty()) return itemStack;
		}

		return findStack(new PlayerMainInvWrapper(inventoryPlayer), stackToMatch);
	}

	private static ItemStack findStack(final IItemHandler itemHandler, final ItemStack stackToMatch) {
		for (final var handlerSlot : ItemSlot.iterable(itemHandler)) {
			final var maybeItemHandler = handlerSlot.getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
			if (maybeItemHandler.isEmpty()) continue;

			// Handle pick block for all items with containers
			if (!SNSConfig.SERVER.allPickBlock.get()) {
				// Not a ContainerItem
				if (!(handlerSlot.getStack().getItem() instanceof ContainerItem)) continue;
			}

			final var foundStack = ItemSlot.stream(maybeItemHandler.get())
					.filter(slot -> ItemStack.isSameItem(slot.getStack(), stackToMatch))
					.map(slot -> slot.extractItem(Integer.MAX_VALUE, false))
					.findFirst();
			if (foundStack.isPresent()) return foundStack.get();
		}

		return ItemStack.EMPTY;
	}
}