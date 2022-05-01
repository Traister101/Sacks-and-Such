package mod.traister101.util.handlers;

import mod.traister101.Main;
import mod.traister101.objects.container.ContainerSack;
import mod.traister101.objects.items.ItemSack;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PickupHandler {
	
	@SubscribeEvent
	public void onPickupItem(EntityItemPickupEvent event) {
		
		Main.getLog().info("Item picked up");
		
		ItemStack stack = event.getItem().getItem();
		
		if(stack.getItem() instanceof ItemTFC) {
			
			Main.getLog().info("Instance of TFC item");
			
			ItemTFC itemPickup = (ItemTFC) stack.getItem();
			if(itemPickup.getSize(stack).isSmallerThan(Size.NORMAL)) {
				
				Main.getLog().info("Item is smaller than normal, this is good means it should go in sack");
				
				for(int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
					if(i == event.getEntityPlayer().inventory.currentItem)
						continue; // prevent item deletion

					ItemStack sack = event.getEntityPlayer().inventory.getStackInSlot(i);
					
				}
			}
		}
	}
	
	
	
	
	
	
	public boolean onItemPickup(EntityPlayer player, EntityItem entityitem) {
		
		ItemStack itemstack = entityitem.getItem();
		if (itemstack.isEmpty()) {
			return false;
		}

		// Do not pick up if a sack is open
		if (player.openContainer instanceof ContainerSack) {
			return false;
		}

		// Make sure to top off manually placed itemstacks in player inventory first
		topOffPlayerInventory(player, itemstack);

		for (ItemStack sackInv : player.inventory.mainInventory) {
			if (itemstack.isEmpty()) {
				break;
			}

			if (sackInv.isEmpty() || !(sackInv.getItem() instanceof ItemSack)) {
				continue;
			}

			ItemSack sack = (ItemSack) sackInv.getItem();
		}

		return itemstack.isEmpty();
	}

	//Tops off stuff in player inventory
	private static void topOffPlayerInventory(EntityPlayer player, ItemStack itemstack) {

		// Add to player inventory first, if there is an incomplete stack in there.
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = player.inventory.getStackInSlot(i);
			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) {
				continue;
			}

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			if (inventoryStack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
				int space = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (space > itemstack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(itemstack.getCount());
					itemstack.setCount(0);
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					itemstack.shrink(space);
				}
			}
		}

	}
}