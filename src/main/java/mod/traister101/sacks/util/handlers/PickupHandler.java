package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.objects.container.ContainerSack;
import mod.traister101.sacks.objects.items.ItemSack;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class PickupHandler {
	
	
	// TODO depending on sack type automatically pick up different types of items. Example: Miner should only pick up ore
	
	@SubscribeEvent
	public void onPickupItem(EntityItemPickupEvent event) {
		ItemStack itemPickup = event.getItem().getItem();
		
		// If the picked up item implements TFC weight/size
		if(itemPickup.getItem() instanceof IItemSize) {
			// If the picked up item is smaller than size normal 
			if(((IItemSize) itemPickup.getItem()).getSize(itemPickup).isSmallerThan(Size.NORMAL)) {
				if (topOffPlayerInventory(event, itemPickup)) {
					
					for(int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
						ItemStack sack = event.getEntityPlayer().inventory.getStackInSlot(i);
						
						if(sack.getItem() instanceof ItemSack) {
							IItemHandler sackInv = sack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
							for (int j = 0; j < sackInv.getSlots(); j++){
								if (sackInv.getStackInSlot(j).getCount() < sackInv.getSlotLimit(j)) {
									ItemStack result = sackInv.insertItem(j, itemPickup, false);
									int numPickedUp = itemPickup.getCount() - result.getCount();
									event.getItem().setItem(result);
									
									if(numPickedUp > 0) {
										playPickupSound(event);
										((EntityPlayerMP) event.getEntityPlayer()).connection.sendPacket(new 
												SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), numPickedUp));
										event.getEntityPlayer().openContainer.detectAndSendChanges();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// Tops off stuff in player inventory
	private static boolean topOffPlayerInventory(EntityItemPickupEvent event, ItemStack stack) {

		// Add to player inventory first, if there is an incomplete stack in there.
		for (int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = event.getEntityPlayer().inventory.getStackInSlot(i);
			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) {
				continue;
			}

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			if (inventoryStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inventoryStack, stack)) {
				int space = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (space > stack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(stack.getCount());
					stack.setCount(0);
					playPickupSound(event);
					return false;
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					stack.shrink(space);
				}
			}
		}
		return true;
	}
	
	// Take a guess
	private static void playPickupSound(EntityItemPickupEvent event) {
		event.setCanceled(true);
		if (!event.getItem().isSilent()) {
			event.getItem().world.playSound(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ,
					SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
					((event.getItem().world.rand.nextFloat() - event.getItem().world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
	}
}