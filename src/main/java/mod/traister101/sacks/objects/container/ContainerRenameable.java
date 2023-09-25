package mod.traister101.sacks.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public abstract class ContainerRenameable extends ExtendedCapacityContainer {

	protected ContainerRenameable(final InventoryPlayer inventoryPlayer, final ItemStack heldStack) {
		super(inventoryPlayer, heldStack);
	}

	public final void updateItemName(final String newName) {
		final Slot slot = getSlot(heldItemIndex);
		final ItemStack itemStack = slot.getStack();

		if (StringUtils.isBlank(newName)) {
			itemStack.clearCustomName();
		} else {
			// Sets new name with no italics
			itemStack.setStackDisplayName(TextFormatting.RESET + newName);
		}
		slot.putStack(itemStack);
		detectAndSendChanges();
	}

	public final int getItemIndex() {
		return heldItemIndex;
	}
}