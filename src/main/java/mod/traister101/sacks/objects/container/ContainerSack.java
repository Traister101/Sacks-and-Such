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
            switch (type.slots) {
                case 1:
                    // 1 slot container
                    addSackSlots(1, 1, 80, 32);
                    break;
                case 4:
                    // 4 slot container
                    addSackSlots(2, 2, 71, 23);
                    break;
                case 8:
                    // 8 slot container
                    addSackSlots(2, 4, 53, 23);
                    break;
                default:
                    final int rows = (int) Math.ceil((double) type.slots / 9);
                    final int columns = type.slots / rows;
                    addSackSlots(rows, columns);
            }
        }
    }

    private void addSackSlots(final int rows, final int columns, final int startX, final int startY) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                addSlotToContainer(new SackSlot(handler, j + i * 18 + 9, startX + j * 18, startY + i * 18));
            }
        }
    }


    private void addSackSlots(final int rows, final int columns) {
        for (int i = 0; i < rows; i++) {
            if (i == rows - 1) {
                for (int j = 0; j < columns - 1; j++) {
                    addSlotToContainer(new SackSlot(handler, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                }
            } else {
                for (int j = 0; j < 9; j++) {
                    addSlotToContainer(new SackSlot(handler, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                }
            }
        }
    }
}