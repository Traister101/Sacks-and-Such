package traister101.sacks.objects.container;

import net.dries007.tfc.objects.container.ContainerItemStack;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerSack extends ContainerItemStack implements ISlotCallback{
	
	public ContainerSack(InventoryPlayer playerInv, ItemStack stack) {
		super(playerInv, stack);

		this.itemIndex +=4;
	}

	@Override
	protected void addContainerSlots() {

		IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (inventory instanceof ISlotCallback) {

			ISlotCallback callback = (ISlotCallback) inventory;
			addSlotToContainer(new SlotCallback(inventory, 0, 71, 23, callback));
			addSlotToContainer(new SlotCallback(inventory, 1, 89, 23, callback));
			addSlotToContainer(new SlotCallback(inventory, 2, 71, 41, callback));
			addSlotToContainer(new SlotCallback(inventory, 3, 89, 41, callback));
		}
	}
}