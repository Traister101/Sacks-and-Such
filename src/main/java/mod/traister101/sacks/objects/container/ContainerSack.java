package mod.traister101.sacks.objects.container;

import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.objects.inventory.slot.SackSlot;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SackType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerSack extends AbstractContainerRenameable {
	
    private final SackType type;
    
    public ContainerSack(InventoryPlayer playerInv, ItemStack heldStack) {
    	// This super constructor call is gross, especially this V because type can't assigned until after the constructor
    	super(playerInv, heldStack, SackType.getSlotCount(((ItemSack) heldStack.getItem()).getType()));
        this.type = ((ItemSack) heldStack.getItem()).getType();
        this.slotStackCap = SackType.getStackCap(type);
        addContainerSlots();
        addPlayerInventorySlots(playerInv);
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