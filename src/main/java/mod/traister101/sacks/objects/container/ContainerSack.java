package mod.traister101.sacks.objects.container;

import javax.annotation.Nonnull;

import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.objects.inventory.slot.SackSlot;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SackType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerSack extends AbstractContainerRenameable {
	
    protected final SackType type;
    protected final int slotAmount;
    protected int itemDragIndex;
    protected boolean isOffhand;
    
    public ContainerSack(InventoryPlayer playerInv, ItemStack heldStack) {
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
	
	// Don't care about warning, needs a type to make the container
	@Override
	@SuppressWarnings(value = "incomplete-switch")
	protected void addContainerSlots() {
		IItemHandler inventory = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (inventory instanceof SackHandler) {
			SackHandler handler = (SackHandler) inventory;
			switch (type) {
			case THATCH:
			case LEATHER:
			case BURLAP:
				// 4 slot container 
				addSlotToContainer(new SackSlot(inventory, 0, 71, 23, handler, 0));
				addSlotToContainer(new SackSlot(inventory, 1, 89, 23, handler, 1));
				addSlotToContainer(new SackSlot(inventory, 2, 71, 41, handler, 2));
				addSlotToContainer(new SackSlot(inventory, 3, 89, 41, handler, 3));
				break;
				
			case MINER:
				// 1 slot container (
				addSlotToContainer(new SackSlot(inventory, 0, 80, 32, handler, 0));
			}
		}
	}
}