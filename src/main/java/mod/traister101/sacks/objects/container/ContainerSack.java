package mod.traister101.sacks.objects.container;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.inventory.capability.AbstractHandler;
import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.objects.inventory.slot.SackSlot;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SackType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ContainerSack extends AbstractContainerRenameable {

    private final SackType type;
    private final IItemHandler handler;

    public ContainerSack(InventoryPlayer playerInv, ItemStack heldStack) {
        // This super constructor call is gross, especially this V because type can't be assigned until after the constructor
        super(playerInv, heldStack, ((ItemSack) heldStack.getItem()).getType().slots);
        this.type = ((ItemSack) heldStack.getItem()).getType();
        this.slotStackCap = type.stackCap;
        this.handler = SNSUtils.getHandler(heldStack);
        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        final boolean toggle = ((AbstractHandler) handler).hasItems();
        SacksNSuch.getNetwork().sendToServer(new TogglePacket(toggle, SNSUtils.ToggleType.ITEMS));
        super.onContainerClosed(playerIn);
    }

    @Override
    protected void addContainerSlots() {
        if (handler instanceof SackHandler) {
            switch (type) {
                case THATCH:
                case LEATHER:
                case BURLAP:
                    //4 slot container
                    addSlotToContainer(new SackSlot(handler, 0, 71, 23));
                    addSlotToContainer(new SackSlot(handler, 1, 89, 23));
                    addSlotToContainer(new SackSlot(handler, 2, 71, 41));
                    addSlotToContainer(new SackSlot(handler, 3, 89, 41));
                    break;

                case MINER:
                    //1 slot container
                    addSlotToContainer(new SackSlot(handler, 0, 80, 32));
                    break;
                case FARMER:
                    //27 slot container (like a chest)
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 9; j++) {
                            addSlotToContainer(new SackSlot(handler, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                        }
                    }
                    break;
                case KNAPSACK:
                    //18 slot container (two rows)
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 9; j++) {
                            addSlotToContainer(new SackSlot(handler, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}