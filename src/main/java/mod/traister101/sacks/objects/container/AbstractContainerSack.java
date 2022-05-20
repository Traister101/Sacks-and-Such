package mod.traister101.sacks.objects.container;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.StringUtils;

import mod.traister101.sacks.util.SackType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

@ParametersAreNonnullByDefault
public abstract class AbstractContainerSack extends Container {
	
    protected final EntityPlayer player;
    protected final SackType type;
    protected ItemStack stack;
    protected int itemIndex;
    protected int itemDragIndex;
    protected boolean isOffhand;
    protected int slotAmount;
    protected String name;
    
    protected AbstractContainerSack(InventoryPlayer playerInv, ItemStack stack, SackType type) {
        this.player = playerInv.player;
        this.stack = stack;
        this.itemDragIndex = playerInv.currentItem;
        this.type = type;
        this.slotAmount = SackType.getSlotCount(type);
        
        if (stack == player.getHeldItemMainhand()) {
            this.itemIndex = playerInv.currentItem + 27; // Mainhand opened inventory
            this.isOffhand = false;
        } else {
            this.itemIndex = -100; // Offhand, so ignore this rule
            this.isOffhand = true;
        }
        
        this.itemIndex += slotAmount;
        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }
    
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
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
    
	@Nonnull
	@Override
	public ItemStack slotClick(int slotID, int dragType, ClickType clickType, EntityPlayer player) {
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
				if (ItemStack.areItemsEqual(slotStack, playerInventory.getItemStack())) return addToStack(slotStack, playerInventory, dragType);
			}
			// Stack size is within the normal bounds. Example: cobble = 64 or less
			if (slotStack.getCount() <= slotStack.getMaxStackSize()) return super.slotClick(slotID, dragType, clickType, player);
			
			return takeFromStack(slotStack, playerInventory, dragType);
		}
		return super.slotClick(slotID, dragType, clickType, player);
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
	
	protected ItemStack addToStack(ItemStack slotStack, InventoryPlayer playerInventory, int dragType) {
		if (!slotStack.isStackable()) return slotStack;
		// Slot stack less or equal to slot cap
		if (slotStack.getCount() <= SackType.getStackCap(type)) {
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
	
	public final void updateItemName(String newName) {
		name = newName;
		
		ItemStack itemStack = getSlot(itemIndex).getStack();
		
		if (StringUtils.isBlank(newName)) {
			itemStack.clearCustomName();
		} else {
			// Sets name with no italics 
			itemStack.setStackDisplayName(TextFormatting.RESET + name);
		}
		updateSackName(itemStack);
	}
	
	private final void updateSackName(ItemStack itemStack) {
		ItemStack stack = itemStack.copy();
		
		Slot slot = inventorySlots.get(itemIndex);
		slot.inventory.setInventorySlotContents(slot.getSlotIndex(), stack);
		detectAndSendChanges();
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
    public final int getContainerItemIndex() {
    	return itemIndex;
    }
    public final int getOpenContainerItemIndex() {
    	Slot slot = inventorySlots.get(itemIndex);
    	return slot.getSlotIndex();
    }
}