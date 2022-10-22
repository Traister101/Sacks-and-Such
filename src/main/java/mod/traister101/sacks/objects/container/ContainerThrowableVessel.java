package mod.traister101.sacks.objects.container;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.objects.inventory.capability.VesselHandler;
import mod.traister101.sacks.objects.inventory.slot.VesselSlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerThrowableVessel extends AbstractContainer {

    public ContainerThrowableVessel(InventoryPlayer playerInv, ItemStack heldStack) {
        super(playerInv, heldStack, 1);
        this.slotStackCap = ConfigSNS.EXPLOSIVE_VESSEL.slotCap;
        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }

    @Override
    protected void addContainerSlots() {
        IItemHandler inventory = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory instanceof VesselHandler) {
            addSlotToContainer(new VesselSlot(inventory, 0, 80, 32));
        }
    }
}