package mod.traister101.sacks.util.handlers;

import java.util.Random;

import javax.annotation.Nonnull;

import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SackType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;

public final class PickupHandler {
	
	@SubscribeEvent
	public void onPickupItem(EntityItemPickupEvent event) {
		ItemStack itemPickup = event.getItem().getItem();
		
		if (topsOffPlayerInventory(event, itemPickup)) return;
		
		for (int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
			ItemStack sackStack = event.getEntityPlayer().inventory.getStackInSlot(i);
			// Not a sack
			if (!(sackStack.getItem() instanceof ItemSack)) continue;
			
			ItemSack sack = (ItemSack) sackStack.getItem();
			SackType type = sack.getType();
			
			// Config pickup disabled for sack type
			if (SackType.canTypeDoAutoPickup(type)) continue;
			// This sack in particular has auto pickup disabled
			if (!SNSUtils.isAutoPickup(sackStack)) continue;
			
			IItemHandler sackInv = SNSUtils.getHandler(sackStack);
			// Can't place in sack for any number of reasons
			if (!canPlaceInSack(type, sackInv, itemPickup)) continue;
			
			for (int j = 0; j < sackInv.getSlots(); j++) {
				if (sackInv.getStackInSlot(j).getCount() < sackInv.getSlotLimit(j)) {
					ItemStack pickupResult = sackInv.insertItem(j, itemPickup, false);
					final int numPickedUp = itemPickup.getCount() - pickupResult.getCount();
					event.getItem().setItem(pickupResult);
					
					if (numPickedUp > 0) {
						playPickupSound(event);
						EntityPlayer player = event.getEntityPlayer();
						SPacketCollectItem packet = new SPacketCollectItem(event.getItem().getEntityId(), player.getEntityId(), numPickedUp);
						((EntityPlayerMP) player).connection.sendPacket(packet);
						player.openContainer.detectAndSendChanges();
						return;
					}
				}
			}
			// If this sack has voiding enabled empty the picked up stack and finish.
			// This means the first valid sack that has voiding enabled will void the pickup stack
			if (SNSUtils.isAutoVoid(sackStack)) {
				itemPickup.setCount(0);
				playPickupSound(event);
				return;
			}
		}
	}
	
	private static boolean canPlaceInSack(@Nonnull SackType type, @Nonnull IItemHandler sackInv, @Nonnull ItemStack itemPickup) {
		for (int j = 0; j < SackType.getSlotCount(type); j++) {
			if (sackInv.isItemValid(j, itemPickup)) return true;
		}
		return false;
	}
	
	// Tops off stuff in player inventory
	private static boolean topsOffPlayerInventory(@Nonnull EntityItemPickupEvent event, @Nonnull ItemStack stack) {
		// Add to player inventory first, if there is an incomplete stack in there.
		for (int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = event.getEntityPlayer().inventory.getStackInSlot(i);
			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) continue;
			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) continue;
			
			if (inventoryStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inventoryStack, stack)) {
				int space = inventoryStack.getMaxStackSize() - inventoryStack.getCount();
				
				if (space >= stack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(stack.getCount());
					stack.setCount(0);
					playPickupSound(event);
					return true;
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					stack.shrink(space);
				}
			}
		}
		return false;
	}
	
	// Take a guess
	private static void playPickupSound(@Nonnull EntityItemPickupEvent event) {
		event.setCanceled(true);
		EntityItem itemEntity = event.getItem();
		if (!itemEntity.isSilent()) {
			final EntityPlayer player = event.getEntityPlayer();
			final double posX = player.posX;
			final double posY = player.posY;
			final double posZ = player.posZ;
			Random itemRand = itemEntity.world.rand;
			itemEntity.world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, 
					((itemRand.nextFloat() - itemRand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
	}
}