package mod.traister101.sns.util.handlers;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.items.*;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import lombok.experimental.UtilityClass;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public final class PickBlockHandler {

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
					.map(equippedCurios -> extractStack(equippedCurios, stackToMatch))
					.orElse(ItemStack.EMPTY);
			if (!itemStack.isEmpty()) return itemStack;
		}

		return extractStack(new PlayerMainInvWrapper(inventoryPlayer), stackToMatch);
	}

	private static ItemStack extractStack(final IItemHandler itemHandler, final ItemStack stackToMatch) {
		final Stream<ItemHandlerSlot> stream;
		// Handle pick block for all items with containers
		if (SNSConfig.SERVER.allPickBlock.get()) {
			stream = ItemSlot.stream(itemHandler);
		} else {
			stream = ItemSlot.stream(itemHandler).filter(ItemSlot.contentsMatch(s -> s.getItem() instanceof ContainerItem));
		}

		return stream.map(ItemSlot.extractCapability(ForgeCapabilities.ITEM_HANDLER))
				.map(LazyOptional::resolve)
				.flatMap(Optional::stream)
				.flatMap(ItemSlot::stream)
				.filter(slot -> ItemStack.isSameItem(slot.getStack(), stackToMatch))
				.map(slot -> slot.extractItem(slot.getStack().getMaxStackSize(), false))
				.findFirst()
				.orElse(ItemStack.EMPTY);
	}
}