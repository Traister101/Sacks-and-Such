package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.PickBlockPacket;
import mod.traister101.sacks.objects.items.ItemSack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public final class PickBlockHandler {

	/**
	 * Handles the client pickblock and informs the server of the event
	 *
	 * @param player The player to handle
	 * @param target The target the player is aiming at
	 */
	@SideOnly(Side.CLIENT)
	public static void onPickBlock(final EntityPlayer player, final RayTraceResult target) {
		final World world = player.world;

		// Let forge handle it first
		if (ForgeHooks.onPickBlock(target, player, world)) return;

		// Only handle pick blocks
		if (target.typeOfHit != RayTraceResult.Type.BLOCK) return;

		final ItemStack stackToSelect;
		{
			final IBlockState blockState = world.getBlockState(target.getBlockPos());
			// Is air block
			if (blockState.getBlock().isAir(blockState, world, target.getBlockPos())) return;
			stackToSelect = blockState.getBlock().getPickBlock(blockState, target, world, target.getBlockPos(), player);
		}

		// Nothing to select
		if (stackToSelect.isEmpty()) return;

		SacksNSuch.getNetwork().sendToServer(new PickBlockPacket(stackToSelect));
	}

	/**
	 * Our handler for the server side of our pick block. Called from the packet sent from the client
	 *
	 * @param player The player we handle
	 * @param stackToSelect The stack to select
	 */
	public static void handlePickBlock(final EntityPlayerMP player, final ItemStack stackToSelect) {
		final InventoryPlayer inventoryPlayer = player.inventory;

		// We only bother checking if there's an empty slot, so we don't have to do any annoying stack merging
		if (!hasSpace(inventoryPlayer)) return;

		final ItemStack foundStack = findStackInItemContainer(inventoryPlayer, stackToSelect);

		// Didn't find a matching ItemStack
		if (foundStack.isEmpty()) return;

		final int slotIndex = inventoryPlayer.getFirstEmptyStack();
		inventoryPlayer.setInventorySlotContents(slotIndex, foundStack);

		if (InventoryPlayer.isHotbar(slotIndex)) {
			inventoryPlayer.currentItem = slotIndex;
		} else {
			inventoryPlayer.pickItem(slotIndex);
		}

		// Magic number for vanilla packet
		final int windowID = -2;

		// This packet results in the following call on the client
		// inventoryPlayer.setInventorySlotContents(inventoryPlayer.currentItem, inventoryPlayer.getStackInSlot(inventoryPlayer.currentItem));
		player.connection.sendPacket(
				new SPacketSetSlot(windowID, inventoryPlayer.currentItem, inventoryPlayer.getStackInSlot(inventoryPlayer.currentItem)));

		// This packet results in the following call on the client
		// inventoryPlayer.setInventorySlotContents(slotIndex, inventoryPlayer.getStackInSlot(slotIndex));
		player.connection.sendPacket(new SPacketSetSlot(windowID, slotIndex, inventoryPlayer.getStackInSlot(slotIndex)));

		// Sets the selected hotbar slot on the client
		player.connection.sendPacket(new SPacketHeldItemChange(inventoryPlayer.currentItem));
	}

	/**
	 * Finds and extracts a matching item stack from an Item Container inside the Player Inventory
	 *
	 * @param inventoryPlayer The player inventory to search
	 * @param stackToMatch The item we are looking for
	 *
	 * @return Extracted ItemStack or an empty ItemStack if one could not be found
	 */
	private static ItemStack findStackInItemContainer(final InventoryPlayer inventoryPlayer, final ItemStack stackToMatch) {
		for (final ItemStack itemContainer : inventoryPlayer.mainInventory) {
			// If it lacks the ITEM_HANDLER_CAPABILITY we can't transfer items so skip
			if (!itemContainer.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) continue;

			// Handle pick block for all items with containers
			if (!ConfigSNS.GLOBAL.allPickBlock) {
				// Not a sack
				if (!(itemContainer.getItem() instanceof ItemSack)) continue;
			}

			final IItemHandler containerInv = itemContainer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			// Checked for null earlier
			assert containerInv != null;

			for (int j = 0; j < containerInv.getSlots(); j++) {
				final ItemStack slotStack = containerInv.getStackInSlot(j);
				if (!slotStack.isItemEqual(stackToMatch)) continue;

				final int extractAmount = slotStack.getMaxStackSize();
				return containerInv.extractItem(j, extractAmount, false);
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Check if there's empty space in the player inventory
	 *
	 * @param inventoryPlayer The player inventory to search
	 *
	 * @return If there's an empty slot
	 */
	private static boolean hasSpace(final InventoryPlayer inventoryPlayer) {
		for (final ItemStack itemStack : inventoryPlayer.mainInventory) {
			if (itemStack.isEmpty()) return true;
		}

		return false;
	}
}