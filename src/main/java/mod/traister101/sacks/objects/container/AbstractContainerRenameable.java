package mod.traister101.sacks.objects.container;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public abstract class AbstractContainerRenameable extends Container {
	
	protected int itemIndex;
    
	public final void updateItemName(String newName) {
		
		ItemStack itemStack = getSlot(itemIndex).getStack();
		
		if (StringUtils.isBlank(newName)) {
			itemStack.clearCustomName();
		} else {
			// Sets new name with no italics 
			itemStack.setStackDisplayName(TextFormatting.RESET + newName);
		}
		updateSackName(itemStack);
	}
	
	private final void updateSackName(ItemStack itemStack) {
		ItemStack stack = itemStack.copy();
		
		Slot slot = inventorySlots.get(itemIndex);
		slot.inventory.setInventorySlotContents(slot.getSlotIndex(), stack);
		detectAndSendChanges();
	}
    
    public final ItemStack getOpenContainerItemStack() {
    	return getSlot(itemIndex).getStack();
    }
}