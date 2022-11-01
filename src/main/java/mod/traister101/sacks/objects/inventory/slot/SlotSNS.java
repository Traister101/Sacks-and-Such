package mod.traister101.sacks.objects.inventory.slot;

import mod.traister101.sacks.objects.inventory.capability.AbstractHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SlotSNS extends SlotItemHandler {

    public SlotSNS(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit() {
        return getItemHandler().getSlotLimit(slotNumber);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return getItemHandler().getStackLimit(slotNumber, stack);
    }

    @Override
    public void putStack(ItemStack stack) {
        getItemHandler().setStackInSlot(slotNumber, stack);
        onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack stackA, ItemStack stackB) {
        getItemHandler().onContentsChanged(slotNumber);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return !getItemHandler().extractItem(slotNumber, 1, true).isEmpty();
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return getItemHandler().isItemValid(slotNumber, stack);
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        return getItemHandler().getStackInSlot(slotNumber);
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return getItemHandler().extractItem(slotNumber, amount, false);
    }

    @Override
    public AbstractHandler getItemHandler() {
        return (AbstractHandler) super.getItemHandler();
    }
}