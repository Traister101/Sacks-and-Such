package mod.traister101.sacks.objects.container;

import javax.annotation.Nonnull;

import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SackType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

abstract class AbstractContainerSack extends AbstractContainerRenameable {
	
    protected final SackType type;
    protected final int slotAmount;
    protected int itemDragIndex;
    protected boolean isOffhand;
    
    protected AbstractContainerSack(InventoryPlayer playerInv, ItemStack heldStack) {
    	super(playerInv, heldStack);
        this.itemDragIndex = playerInv.currentItem;
        this.type = ((ItemSack) heldStack.getItem()).getType();
        this.slotAmount = SackType.getSlotCount(type);
        
        if (heldStack == player.getHeldItemMainhand()) {
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
				if (ItemStack.areItemsEqual(slotStack, playerInventory.getItemStack())) return addToStack(slotStack, playerInventory, dragType, SackType.getStackCap(type));
			}
			// Stack size is within the normal bounds. Example: cobble = 64 or less
			if (slotStack.getCount() <= slotStack.getMaxStackSize()) return super.slotClick(slotID, dragType, clickType, player);
			
			return takeFromStack(slotStack, playerInventory, dragType);
		}
		return super.slotClick(slotID, dragType, clickType, player);
	}
}