package mod.traister101.sacks.objects.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractContainer extends Container {
    
    protected final ItemStack heldStack;
    protected final EntityPlayer player;
    protected final int slotAmount;
    protected boolean isOffhand;
    protected int slotStackCap;
	protected int itemIndex;
    
    protected AbstractContainer(InventoryPlayer playerInv, ItemStack heldStack, int slotAmount) {
        this.player = playerInv.player;
    	this.heldStack = heldStack;
    	this.slotAmount = slotAmount;
        if (heldStack == player.getHeldItemMainhand()) {
            this.itemIndex = playerInv.currentItem + 27; // Mainhand opened inventory
            this.isOffhand = false;
        } else {
            this.itemIndex = -100; // Offhand, so ignore this rule
            this.isOffhand = true;
        }
    	this.itemIndex += slotAmount;
	}
    
	@Override
	protected final boolean mergeItemStack(ItemStack stackIn, int startIndex, int endIndex, boolean reverseDirection) {
		boolean flag = false;
		int i = startIndex;
		
		if (reverseDirection) {
			i = endIndex - 1;
		}
		
		while (!stackIn.isEmpty()) {
			if (reverseDirection) {
				if (i < startIndex)
					break;
			} else {
				if (i >= endIndex)
					break;
			}
			
			Slot slot = inventorySlots.get(i);
			ItemStack itemStack = slot.getStack();
			
			if (!itemStack.isEmpty() && itemStack.getItem() == stackIn.getItem() && (stackIn.getMetadata() == itemStack.getMetadata()) && ItemStack.areItemStackTagsEqual(stackIn, itemStack)) {
				int totalSize = itemStack.getCount() + stackIn.getCount();
				int maxSize = slot.getItemStackLimit(itemStack);
				// TODO max size should split stacks into smaller stacks
//				if (maxSize > itemStack.getMaxStackSize()) maxSize = itemStack.getMaxStackSize();
				
				if (totalSize <= maxSize) {
					stackIn.setCount(0);
					itemStack.setCount(totalSize);
					slot.onSlotChanged();
					flag = true;
				} else if (itemStack.getCount() < maxSize) {
					stackIn.shrink(maxSize - itemStack.getCount());
					itemStack.setCount(maxSize);
					slot.onSlotChanged();
					flag = true;
				}
			}
			i += (reverseDirection) ? -1 : 1;
		}
		
		if (!stackIn.isEmpty()) {
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
				
				if (itemstack1.isEmpty() && slot1.isItemValid(stackIn)) {
					if (stackIn.getCount() > slot1.getItemStackLimit(stackIn)) {
						slot1.putStack(stackIn.splitStack(slot1.getItemStackLimit(stackIn)));
					} else {
						slot1.putStack(stackIn.splitStack(stackIn.getCount()));
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
	
	@Nonnull
	@Override
	public final ItemStack slotClick(int slotID, int dragType, ClickType clickType, EntityPlayer player) {
		// Not a slot, let vanilla handle it
		if (slotID < 0) return super.slotClick(slotID, dragType, clickType, player);
		// Prevent moving of the item stack that is currently open
		if (slotID == itemIndex) return ItemStack.EMPTY;
		// Vanilla slot
		if (slotID > slotAmount) return super.slotClick(slotID, dragType, clickType, player);
 		// Shift click, vanilla method works fine
 		if (clickType == ClickType.QUICK_MOVE) return super.slotClick(slotID, dragType, clickType, player);
		
		Slot slot = inventorySlots.get(slotID);
		ItemStack slotStack = slot.getStack();
		// Slot is empty give to vanilla
		if (slotStack.isEmpty()) return super.slotClick(slotID, dragType, clickType, player);
		
		if (clickType == ClickType.PICKUP) {
			InventoryPlayer playerInventory = player.inventory;
			
			// Holding a item
			if (!playerInventory.getItemStack().isEmpty()) {
				// Stack is full
				if (slotStack.getCount() >= slot.getSlotStackLimit()) return slotStack;
				// Stacks are the same
				if (ItemStack.areItemsEqual(slotStack, playerInventory.getItemStack())) return addToStack(slotStack, playerInventory, dragType, slotStackCap);
			}
			// Stack size is within the normal bounds. Example: cobble = 64 or less
			if (slotStack.getCount() <= slotStack.getMaxStackSize()) return super.slotClick(slotID, dragType, clickType, player);
			
			return takeFromStack(slotStack, playerInventory, dragType);
		}
		return super.slotClick(slotID, dragType, clickType, player);
	}
	
	@Override
	public final ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (index < slotAmount) {
				if (!mergeItemStack(itemstack1, slotAmount, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(itemstack1, 0, slotAmount, false)) {
				return ItemStack.EMPTY;
			}
			
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}
	
	private ItemStack takeFromStack(ItemStack slotStack, InventoryPlayer playerInventory, int dragType) {
		int amount = slotStack.getMaxStackSize();
		if (dragType == 1) amount = amount / 2;
		
		ItemStack mouseStack = slotStack.copy();
		slotStack.setCount(slotStack.getCount() - amount);
		mouseStack.setCount(amount);
		playerInventory.setItemStack(mouseStack);
		return slotStack;
	}
	
	private ItemStack addToStack(ItemStack slotStack, InventoryPlayer playerInventory, int dragType, int stackCap) {
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
    
    protected final void addPlayerInventorySlots(InventoryPlayer playerInv) {
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