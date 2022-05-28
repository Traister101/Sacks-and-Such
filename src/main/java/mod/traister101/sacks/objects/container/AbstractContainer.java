package mod.traister101.sacks.objects.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractContainer extends Container {
    
    protected final ItemStack heldStack;
    protected final EntityPlayer player;
    
    public AbstractContainer(InventoryPlayer playerInv, ItemStack heldStack) {
        this.player = playerInv.player;
    	this.heldStack = heldStack;
	}
    
	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		boolean flag = false;
		int i = startIndex;
		
		if (reverseDirection) {
			i = endIndex - 1;
		}
		
		while (!stack.isEmpty()) {
			if (reverseDirection) {
				if (i < startIndex)
					break;
			} else {
				if (i >= endIndex)
					break;
			}
			
			Slot slot = inventorySlots.get(i);
			ItemStack itemStack = slot.getStack();
			
			if (!itemStack.isEmpty() && itemStack.getItem() == stack.getItem()
					&& (stack.getMetadata() == itemStack.getMetadata())
					&& ItemStack.areItemStackTagsEqual(stack, itemStack)) {
				int totalSize = itemStack.getCount() + stack.getCount();
				int maxSize = slot.getItemStackLimit(itemStack);
				// TODO max size should split stacks into smaller stacks
//				if (maxSize > itemStack.getMaxStackSize()) maxSize = itemStack.getMaxStackSize();
				
				if (totalSize <= maxSize) {
					stack.setCount(0);
					itemStack.setCount(totalSize);
					slot.onSlotChanged();
					flag = true;
				} else if (itemStack.getCount() < maxSize) {
					stack.shrink(maxSize - itemStack.getCount());
					itemStack.setCount(maxSize);
					slot.onSlotChanged();
					flag = true;
				}
			}
			i += (reverseDirection) ? -1 : 1;
		}
		
		if (!stack.isEmpty()) {
			if (reverseDirection) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}
			while (true) {
				if (reverseDirection) {
					if (i < startIndex)
						break;
				} else {
					if (i >= endIndex)
						break;
				}
				
				Slot slot1 = inventorySlots.get(i);
				ItemStack itemstack1 = slot1.getStack();
				
				if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
					if (stack.getCount() > slot1.getItemStackLimit(stack)) {
						slot1.putStack(stack.splitStack(slot1.getItemStackLimit(stack)));
					} else {
						slot1.putStack(stack.splitStack(stack.getCount()));
					}
					slot1.onSlotChanged();
					flag = true;
					break;
				}
				i += (reverseDirection) ? -1 : 1;
			}
		}
		return flag;
	}
	
	protected ItemStack takeFromStack(ItemStack slotStack, InventoryPlayer playerInventory, int dragType) {
		int amount = slotStack.getMaxStackSize();
		if (dragType == 1) amount = amount / 2;
		
		ItemStack mouseStack = slotStack.copy();
		slotStack.setCount(slotStack.getCount() - amount);
		mouseStack.setCount(amount);
		playerInventory.setItemStack(mouseStack);
		return slotStack;
	}
	
	protected ItemStack addToStack(ItemStack slotStack, InventoryPlayer playerInventory, int dragType, int stackCap) {
		if (!slotStack.isStackable()) return slotStack;
		// Slot stack less or equal to slot cap
		if (slotStack.getCount() <= stackCap) {
			if (dragType == 0) {
				slotStack.setCount(slotStack.getCount() + playerInventory.getItemStack().getCount());
				playerInventory.setItemStack(ItemStack.EMPTY);
				return slotStack;
			} else {
				ItemStack mouseStack = playerInventory.getItemStack();
				slotStack.setCount(slotStack.getCount() + 1);
				mouseStack.setCount(mouseStack.getCount() - 1);
				playerInventory.setItemStack(mouseStack);
				return slotStack;
			}
		}
		return slotStack;
	}
	
	protected abstract void addContainerSlots();
    
    protected void addPlayerInventorySlots(InventoryPlayer playerInv) {
        // Add Player Inventory Slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
    	return true;
    }
}