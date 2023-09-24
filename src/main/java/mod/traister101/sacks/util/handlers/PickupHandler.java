package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.api.types.SackType;
import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SNSUtils;
import net.dries007.tfc.objects.blocks.BlockPlacedItemFlat;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;

public final class PickupHandler {

	/**
	 * Tries to first fill any valid stacks in the player inventory then tries to fill any {@link ItemSack}s. If both fail to consume
	 * the entire stack the remainer is returned
	 *
	 * @param player Player to handle
	 * @param itemPickup The item being picked up
	 *
	 * @return Empty {@link ItemStack} or the remainer.
	 */
	private static ItemStack pickupItemStack(final EntityPlayer player, final ItemStack itemPickup) {

		if (topOffPlayerInventory(player.inventory, itemPickup)) return ItemStack.EMPTY;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			final ItemStack itemContainer = player.inventory.getStackInSlot(i);

			// If it lacks the ITEM_HANDLER_CAPABILITY we can't transfer items so skip
			if (!itemContainer.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) continue;

			// Handle item pickups for all items with containers
			if (!ConfigSNS.GLOBAL.allPickup) {
				// Not a sack
				if (!(itemContainer.getItem() instanceof ItemSack)) continue;
			}

			// Make sure the item is a Sack, so we don't crash when other items can pick up too
			if (itemContainer.getItem() instanceof ItemSack) {
				final SackType type = ((ItemSack) itemContainer.getItem()).getType();
				// Config pickup disabled for sack type
				if (!type.doesAutoPickup()) continue;
				// This sack in particular has auto pickup disabled
				if (!NBTHelper.isAutoPickup(itemContainer)) continue;
			}

			final IItemHandler containerInv = itemContainer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			// Checked for null earlier
			assert containerInv != null;

			// We can't place in the container for any number of reasons
			if (!isValidForContainer(containerInv, itemPickup)) continue;

			// Goes through the sack slots to see if the picked up item can be added
			for (int j = 0; j < containerInv.getSlots(); j++) {
				if (containerInv.getStackInSlot(j).getCount() < containerInv.getSlotLimit(j)) {
					final ItemStack pickupResult = containerInv.insertItem(j, itemPickup, false);
					final int numPickedUp = itemPickup.getCount() - pickupResult.getCount();

					if (0 < numPickedUp) {
						player.openContainer.detectAndSendChanges();
						if (containerInv instanceof SackHandler) {
							final boolean toggleFlag = ((SackHandler) containerInv).hasItems();
							SNSUtils.toggle(itemContainer, SNSUtils.ToggleType.ITEMS, toggleFlag);
						}
					}

					if (pickupResult.isEmpty()) {
						return ItemStack.EMPTY;
					} else {
						itemPickup.setCount(pickupResult.getCount());
					}
				}
			}
			// Can't void
			if (!canItemVoid(itemContainer)) continue;
			// Make sure there's a slot with the same type of item before voiding the pickup
			for (int j = 0; j < containerInv.getSlots(); j++) {
				final ItemStack slotStack = containerInv.getStackInSlot(j);
				if (ItemStack.areItemsEqual(slotStack, itemPickup)) {
					itemPickup.setCount(0);
					return ItemStack.EMPTY;
				}
			}
		}
		return itemPickup;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean canItemVoid(final ItemStack itemContainer) {
		// Item voiding disabled
		if (!ConfigSNS.GLOBAL.doVoiding) return false;
		// Not a sack
		if (!(itemContainer.getItem() instanceof ItemSack)) return false;
		// Type can't void items
		if (!((ItemSack) itemContainer.getItem()).getType().doesVoiding()) return false;
		// Returns if this particular sack item has voiding enabled
		return NBTHelper.isAutoVoid(itemContainer);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isValidForContainer(final IItemHandler containerInv, final ItemStack itemPickup) {
		for (int i = 0; i < containerInv.getSlots(); i++) {
			if (containerInv.isItemValid(i, itemPickup)) return true;
		}
		return false;
	}

	/**
	 * Tops off the player inventory consuming the itemstack until all stacks in the inventory are filled
	 *
	 * @param inventoryPlayer Player inventory we should top up
	 * @param itemStack The itemstack we consume to fill the inventory
	 *
	 * @return If the item stack was fully consumed
	 */
	private static boolean topOffPlayerInventory(final InventoryPlayer inventoryPlayer, final ItemStack itemStack) {
		// Add to player inventory first, if there is an incomplete stack in there.
		for (int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
			final ItemStack inventoryStack = inventoryPlayer.getStackInSlot(i);

			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) continue;

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) continue;

			// Can merge stacks
			if (inventoryStack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(inventoryStack, itemStack)) {
				final int remainingSpace = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (remainingSpace >= itemStack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(itemStack.getCount());
					itemStack.setCount(0);
					return true;
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					itemStack.shrink(remainingSpace);
				}
			}
		}
		return false;
	}

	/**
	 * Take a guess
	 *
	 * @param world The world to play the sound in
	 * @param pos The position to play the sound at
	 */
	private static void playPickupSound(final World world, final Vec3d pos) {
		final Random rand = world.rand;
		world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
				((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void onPickupItem(final EntityItemPickupEvent event) {
		final EntityPlayer player = event.getEntityPlayer();
		final EntityItem itemEntity = event.getItem();
		final ItemStack itemResult;
		final int pickupCount;
		{
			final ItemStack stack = itemEntity.getItem();
			final int startCount = stack.getCount();
			itemResult = pickupItemStack(player, stack);
			pickupCount = startCount - itemResult.getCount();
		}

		// Update the item entity
		itemEntity.setItem(itemResult);

		// Picked up more than 0
		if (0 < pickupCount) {
			final SPacketCollectItem packet = new SPacketCollectItem(itemEntity.getEntityId(), player.getEntityId(), pickupCount);
			((EntityPlayerMP) player).connection.sendPacket(packet);
			playPickupSound(player.world, player.getPositionVector());
		}

		event.setCanceled(itemResult.isEmpty());
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void onBlockActivated(final RightClickBlock event) {
		final BlockPos blockPos = event.getPos();
		final World world = event.getWorld();
		final IBlockState blockState = world.getBlockState(blockPos);

		// TFC flat item block
		if (blockState.getBlock() instanceof BlockPlacedItemFlat) {
			final TEPlacedItemFlat placedItem = Helpers.getTE(world, blockPos, TEPlacedItemFlat.class);
			final ItemStack stack = placedItem.getStack();
			placedItem.setStack(ItemStack.EMPTY);
			world.setBlockToAir(blockPos);
			final EntityPlayer player = event.getEntityPlayer();

			if (event.getSide() == Side.SERVER) {
				final ItemStack itemResult = pickupItemStack(player, stack);

				// Result being empty
				if (!itemResult.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, itemResult);
				} else {
					playPickupSound(player.world, player.getPositionVector());
				}
			}

			player.swingArm(EnumHand.MAIN_HAND);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			event.setCanceled(true);
		}
	}
}