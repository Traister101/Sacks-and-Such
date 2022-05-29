package mod.traister101.sacks.objects.inventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;

public class SackHandler extends AbstractHandler {
	
	private final SackType type;
	
	public SackHandler(@Nullable NBTTagCompound nbt, SackType type) {
		super(nbt, SackType.getSlotCount(type), SackType.getStackCap(type));
		this.type = type; 
	}
	
	// TODO Sack type should determine what types of items are valid. Example: Miner should only pick up ore
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if (stack.isEmpty()) return false;
		if (!stack.isStackable()) return false;
		// Stack is a sack, no sack-ception
		if (stack.getItem() instanceof ItemSack) return false;
		// If the item is larger than normal
		if (stack.getItem() instanceof IItemSize) {
			if (!((IItemSize) stack.getItem()).getSize(stack).isSmallerThan(Size.NORMAL)) return false;
		}
		
		ItemStack currentStack = getStackInSlot(slot);
		setStackInSlot(slot, ItemStack.EMPTY);
		ItemStack remainder = insertItem(slot, stack, true);
		setStackInSlot(slot, currentStack);
		
		return remainder.isEmpty() || remainder.getCount() < stack.getCount();
	}
	
	@Override
	public void onContentsChanged(int slot) {
		
	}
}