package mod.traister101.sacks.objects.inventory.slot;

import javax.annotation.Nonnull;

import mod.traister101.sacks.objects.inventory.capability.AbstractHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class AbstractSlot extends SlotItemHandler {
	
	protected final AbstractHandler handler;
	
	public AbstractSlot(@Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.handler = (AbstractHandler) itemHandler;
	}
	
	@Override
	public int getSlotStackLimit() {
		return handler.getSlotLimit(slotNumber);
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return handler.getSlotLimit(slotNumber);
	}
	
	@Override
	public void putStack(@Nonnull ItemStack stack) {
		handler.setStackInSlot(slotNumber, stack);
		onSlotChanged();
	}
	
	@Override
	public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
		handler.onContentsChanged(slotNumber);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !handler.extractItem(slotNumber, 1, true).isEmpty();
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return handler.isItemValid(slotNumber, stack);
	}
	
	@Override
	@Nonnull
	public ItemStack getStack() {
		return handler.getStackInSlot(slotNumber);
	}
	
	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		return handler.extractItem(slotNumber, amount, false);
	}
	
	@Override
	public AbstractHandler getItemHandler() {
		return handler;
	}
}