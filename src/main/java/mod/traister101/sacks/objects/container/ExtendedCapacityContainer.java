package mod.traister101.sacks.objects.container;

import mcp.MethodsReturnNonnullByDefault;
import mod.traister101.sacks.objects.inventory.capability.AbstractHandler;
import mod.traister101.sacks.objects.inventory.slot.SlotSNS;
import mod.traister101.sacks.util.SNSUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ExtendedCapacityContainer extends Container {

    protected final IItemHandler handler;
    protected final EntityPlayer player;
    protected final int slotAmount;
    protected boolean isOffhand;
    protected int slotStackCap;
    protected int itemIndex;

    protected ExtendedCapacityContainer(InventoryPlayer playerInv, ItemStack heldStack) {
        this.player = playerInv.player;
        this.handler = SNSUtils.getHandler(heldStack);
        if (handler == null) {
            throw new NullPointerException("This stack doesn't have a Item handler! How did you open this?");
        }
        this.slotAmount = handler.getSlots();
        this.slotStackCap = handler.getSlotLimit(0);
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
    protected final boolean mergeItemStack(ItemStack stackIn, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;

        if (reverseDirection) i = endIndex - 1;

        while (!stackIn.isEmpty()) {
            if (reverseDirection) {
                if (i < startIndex) break;
            } else if (i >= endIndex) break;

            final Slot slot = getSlot(i);
            ItemStack slotStack = slot.getStack();

            if (!slotStack.isEmpty() && slotStack.getItem() == stackIn.getItem() && (stackIn.getMetadata() == slotStack.getMetadata()) && ItemStack.areItemStackTagsEqual(stackIn, slotStack)) {

                if (i < slotAmount) {
                    final ItemStack remainer = handler.insertItem(i, stackIn, false);
                    stackIn.setCount(remainer.getCount());
                } else {
                    final int total = slotStack.getCount() + stackIn.getCount();
                    final int maxSize = Math.min(slot.getSlotStackLimit(), stackIn.getMaxStackSize());
                    if (total <= maxSize) {
                        stackIn.setCount(0);
                        slotStack.setCount(total);
                    } else if (slotStack.getCount() < maxSize) {
                        stackIn.shrink(maxSize - slotStack.getCount());
                        slotStack.setCount(slotStack.getMaxStackSize());
                    }
                    slot.onSlotChanged();
                    flag = true;
                }

            }
            i += (reverseDirection) ? -1 : 1;
        }

        if (!stackIn.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else i = startIndex;

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) break;
                } else if (i >= endIndex) break;

                final Slot slot = getSlot(i);
                final ItemStack slotStack = slot.getStack();

                if (slotStack.isEmpty() && slot.isItemValid(stackIn)) {
                    if (i < slotAmount) {
                        slot.putStack(stackIn.splitStack(slotStackCap));
                    } else if (stackIn.getCount() > stackIn.getMaxStackSize()) {
                        slot.putStack(stackIn.splitStack(stackIn.getMaxStackSize()));
                    } else {
                        slot.putStack(stackIn.splitStack(stackIn.getCount()));
                    }
                    slot.onSlotChanged();
                    flag = true;
                }
                i += (reverseDirection) ? -1 : 1;
            }
        }
        return flag;
    }

    @Override
    public final ItemStack slotClick(final int slotID, final int dragType, final ClickType clickType, final EntityPlayer player) {
        // Not a slot, let vanilla handle it
        if (slotID < 0) return super.slotClick(slotID, dragType, clickType, player);
        // Prevent moving of the item stack that is currently open
        if (slotID == itemIndex) return ItemStack.EMPTY;
        // Keybind swap
        if (clickType == ClickType.SWAP && dragType >= 0 && dragType < 9) {
            final ItemStack hoverStack = inventorySlots.get(slotID).getStack();
            final ItemStack hotbarStack = player.inventory.getStackInSlot(dragType);
            // Blocks the held stack from being moved
            if (hoverStack == player.getHeldItemMainhand() || player.getHeldItemMainhand() == hotbarStack)
                return ItemStack.EMPTY;

            // If either stack is empty pass it to vanilla
            if (hotbarStack.isEmpty() || hoverStack.isEmpty())
                return super.slotClick(slotID, dragType, clickType, player);

            // If neither stack is too large pass to vanilla
            if (hotbarStack.getCount() <= hotbarStack.getMaxStackSize() && hoverStack.getCount() <= hoverStack.getMaxStackSize())
                return super.slotClick(slotID, dragType, clickType, player);

            return ItemStack.EMPTY;
        }
        // Vanilla slot, pass to vanilla
        if (slotID > slotAmount - 1) return super.slotClick(slotID, dragType, clickType, player);
        // Shift click, vanilla method works fine
        if (clickType == ClickType.QUICK_MOVE) return super.slotClick(slotID, dragType, clickType, player);

        final Slot slot = getSlot(slotID);
        final ItemStack slotStack = slot.getStack();
        // Slot is empty give to vanilla
        if (slotStack.isEmpty()) return super.slotClick(slotID, dragType, clickType, player);

        // Pressing q
        if (clickType == ClickType.THROW && player.inventory.getItemStack().isEmpty()) {
            // ctrl + q
            if (dragType == 1) {
                final ItemStack tossStack = handler.extractItem(slotID, slotStack.getMaxStackSize(), false);
                player.dropItem(tossStack, false);
                return slotStack;
            } else return super.slotClick(slotID, dragType, clickType, player);
        }

        if (clickType == ClickType.PICKUP) {
            final InventoryPlayer playerInventory = player.inventory;
            final ItemStack mouseStack = playerInventory.getItemStack();

            // Holding an item
            if (!mouseStack.isEmpty()) {
                // If we should try to swap the stacks
                if (!ItemStack.areItemsEqual(slotStack, mouseStack)) {
                    if (isStacksSwappable(slotStack, mouseStack, slotID)) {
                        slot.putStack(mouseStack);
                        playerInventory.setItemStack(slotStack);
                        return mouseStack;
                    } else {
                        return slotStack;
                    }
                }

                // Stack is full
                if (slotStack.getCount() >= slot.getSlotStackLimit()) return slotStack;

                // If we should try to merge the stacks
                if (ItemStack.areItemsEqual(slotStack, mouseStack)) {
                    if (slotStack.isStackable() && slotStack.getCount() <= slotStackCap) {
                        if (dragType == 0) {
                            // This is a left click
                            final ItemStack remainer = handler.insertItem(slotID, mouseStack, false);
                            playerInventory.setItemStack(remainer);
                        } else {
                            // This is a rightclick
                            slotStack.setCount(slotStack.getCount() + 1);
                            mouseStack.setCount(mouseStack.getCount() - 1);
                            playerInventory.setItemStack(mouseStack);
                        }
                        return slotStack;
                    }
                }
            }
            //Stack size is within the normal bounds. Example: cobble = 64 or less
            if (slotStack.getCount() <= slotStack.getMaxStackSize())
                return super.slotClick(slotID, dragType, clickType, player);

            // Grabbing items from sack
            if (mouseStack.isEmpty()) {
                int amount = slotStack.getMaxStackSize();
                if (dragType == 1) amount = amount / 2;
                final ItemStack extractedItem = handler.extractItem(slotID, amount, false);
                playerInventory.setItemStack(extractedItem);
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotID, dragType, clickType, player);
    }

    // This is very much needed don't believe the fact it seems to be unused
    @Override
    public final ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            final ItemStack slotStack = slot.getStack();

            if (index < slotAmount) {
                if (!mergeItemStack(slotStack, slotAmount, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(slotStack, 0, slotAmount, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    private boolean isStacksSwappable(final ItemStack stackA, final ItemStack stackB, final int slotID) {
        final int countA = stackA.getCount();
        final int countB = stackB.getCount();

        boolean swappableA = false;
        boolean swappableB = false;

        if (countA <= slotStackCap && countA <= stackA.getMaxStackSize())
            swappableA = true;

        if (countB <= slotStackCap && countB <= stackB.getMaxStackSize() && handler.isItemValid(slotID, stackB))
            swappableB = true;

        return swappableA && swappableB;
    }

    protected void addContainerSlots() {
        if (handler instanceof AbstractHandler) {
            switch (handler.getSlots()) {
                case 1:
                    // 1 slot container
                    addSlots(1, 1, 80, 32);
                    break;
                case 4:
                    // 4 slot container
                    addSlots(2, 2, 71, 23);
                    break;
                case 8:
                    // 8 slot container
                    addSlots(2, 4, 53, 23);
                    break;
                case 18:
                    addSlots(2, 9, 8, 34);
                    break;
                default:
                    final int rows = (int) Math.ceil((double) handler.getSlots() / 9);
                    final int columns = handler.getSlots() / rows;
                    addSlots(rows, columns);
            }
        }
    }

    private void addSlots(final int rows, final int columns, final int startX, final int startY) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                addSlotToContainer(new SlotSNS(handler, j + i * 18 + 9, startX + j * 18, startY + i * 18));
            }
        }
    }

    private void addSlots(final int rows, final int columns) {
        for (int i = 0; i < rows; i++) {
            if (i == rows - 1) {
                for (int j = 0; j < columns; j++) {
                    addSlotToContainer(new SlotSNS(handler, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                }
            } else {
                for (int j = 0; j < 9; j++) {
                    addSlotToContainer(new SlotSNS(handler, j + i * 18 + 9, 8 + j * 18, 27 + i * 18));
                }
            }
        }
    }

    protected final void addPlayerInventorySlots(InventoryPlayer playerInv) {
        // Add Player Inventory Slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        // Add player hotbar slots
        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}