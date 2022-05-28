package mod.traister101.sacks.objects.inventory.slot;

import javax.annotation.Nonnull;

import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SackSlot extends SlotItemHandler {
	
	private final SackHandler handler;
	private final int index;
	
	public SackSlot(@Nonnull IItemHandler inventory, int idx, int x, int y, @Nonnull SackHandler handler, int index) {
        super(inventory, idx, x, y);
        this.handler = handler;
        this.index = index;
    }
	
	@Override
	public int getSlotStackLimit() {
		return Math.min(handler.getSlotLimit(getSlotIndex()), super.getSlotStackLimit());
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return handler.getSlotLimit(slotNumber);
	}
	
	@Override
	public void putStack(@Nonnull ItemStack stack) {
		getItemHandler().setStackInSlot(index, stack);
		onSlotChanged();
	}
	
	@Override
	public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
		getItemHandler().onContentsChanged(index);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !getItemHandler().extractItem(index, 1, true).isEmpty();
	}
	
	@Override
	public SackHandler getItemHandler() {
		return handler;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return handler.isItemValid(index, stack);
	}
	
	@Override
	@Nonnull
	public ItemStack getStack() {
		return getItemHandler().getStackInSlot(index);
	}
	
	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		return getItemHandler().extractItem(index, amount, false);
	}
	
	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof SackSlot && ((SackSlot) other).getItemHandler() == handler;
	}
}