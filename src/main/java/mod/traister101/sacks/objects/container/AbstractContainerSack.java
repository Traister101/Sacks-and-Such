package mod.traister101.sacks.objects.container;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@ParametersAreNonnullByDefault
public abstract class AbstractContainerSack extends Container implements ISlotCallback {
	
    protected final ItemStack stack;
    protected final EntityPlayer player;
    protected int itemIndex;
    protected int itemDragIndex;
    protected boolean isOffhand;
    protected SackType type;
    
    protected AbstractContainerSack(InventoryPlayer playerInv, ItemStack stack, SackType type) {
        this.player = playerInv.player;
        this.stack = stack;
        this.itemDragIndex = playerInv.currentItem;
        this.type = type;
        this.itemIndex = SackType.getSlotsForType(type);
        
        if (stack == player.getHeldItemMainhand()) {
            this.itemIndex = playerInv.currentItem + 27; // Mainhand opened inventory
            this.isOffhand = false;
        } else {
            this.itemIndex = -100; // Offhand, so ignore this rule
            this.isOffhand = true;
        }
        
        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }
    
    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        // Slot that was clicked
        Slot slot = inventorySlots.get(index);
        ItemStack itemstack;
        
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        if (index == itemIndex) return ItemStack.EMPTY;
        
        ItemStack itemstack1 = slot.getStack();
        itemstack = itemstack1.copy();
        // Begin custom transfer code here
        int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size(); // number of slots in the container
        if (index < containerSlots) {
            // Transfer out of the container
            if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
                // Don't transfer anything
                return ItemStack.EMPTY;
            }
        }
        // Transfer into the container
        else {
            if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }
        }
        
        if (itemstack1.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        
        if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
        }
        
        slot.onTake(player, itemstack1);
        return itemstack;
    }
    
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
			
			Slot slot = this.inventorySlots.get(i);
			ItemStack itemstack = slot.getStack();
			
			if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem()
					&& (stack.getMetadata() == itemstack.getMetadata())
					&& ItemStack.areItemStackTagsEqual(stack, itemstack)) {
				int j = itemstack.getCount() + stack.getCount();
				int maxSize = slot.getItemStackLimit(itemstack);
				
				if (j <= maxSize) {
					stack.setCount(0);
					itemstack.setCount(j);
					slot.onSlotChanged();
					flag = true;
				} else if (itemstack.getCount() < maxSize) {
					stack.shrink(maxSize - itemstack.getCount());
					itemstack.setCount(maxSize);
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
				
				Slot slot1 = this.inventorySlots.get(i);
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
    
	
	// TODO Limit this to stacks of 64 
    @Override
    @Nonnull
    public ItemStack slotClick(int slotID, int dragType, ClickType clickType, EntityPlayer player) {
    	ItemStack itemStack = ItemStack.EMPTY;
    	InventoryPlayer playerInventory = player.inventory;
        // Prevent moving of the item stack that is currently open
        if (slotID == itemIndex && (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP || clickType == ClickType.THROW || clickType == ClickType.SWAP)) {
            return ItemStack.EMPTY;
        }
        else if ((dragType == itemDragIndex) && clickType == ClickType.SWAP) {
            return ItemStack.EMPTY;
        } else 
            return super.slotClick(slotID, dragType, clickType, player);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
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
}