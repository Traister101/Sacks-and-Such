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
        super(playerInv, heldStack, ((ItemSack) heldStack.getItem()).getType().slots);
        this.type = ((ItemSack) heldStack.getItem()).getType();
        this.slotStackCap = type.stackCap;
        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }

    @Override
    protected void addContainerSlots() {
        IItemHandler inventory = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory instanceof SackHandler) {
            switch (type) {
                case THATCH:
                case LEATHER:
                case BURLAP:
                    //4 slot container
                    addSlotToContainer(new SackSlot(inventory, 0, 71, 23));
                    addSlotToContainer(new SackSlot(inventory, 1, 89, 23));
                    addSlotToContainer(new SackSlot(inventory, 2, 71, 41));
                    addSlotToContainer(new SackSlot(inventory, 3, 89, 41));
                    break;

                case MINER:
                    //1 slot container
                    addSlotToContainer(new SackSlot(inventory, 0, 80, 32));
                    break;
                case FARMER:
                    //27 slot container (like a chest)
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 9; j++) {
                            addSlotToContainer(new SackSlot(inventory, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                        }
                    }
                    break;
                case KNAPSACK:
                    //18 slot container (two rows)
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 9; j++) {
                            addSlotToContainer(new SackSlot(inventory, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}