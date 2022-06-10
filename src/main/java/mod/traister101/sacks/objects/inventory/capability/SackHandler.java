package mod.traister101.sacks.objects.inventory.capability;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
		
		// Is the miner sack and it can't pickup non ore items
		if (type == SackType.MINER && !ConfigSNS.MINERSACK.allowNonOre) {
			if (!(stack.getItem() instanceof ItemOreTFC || stack.getItem() instanceof ItemSmallOre)) return false;
		}
		// If the item is larger than normal
		if (stack.getItem() instanceof IItemSize) {
			if (!((IItemSize) stack.getItem()).getSize(stack).isSmallerThan(SackType.getSlotSize(type))) return false;
		}
		
		ItemStack currentStack = getStackInSlot(slot);
		setStackInSlot(slot, ItemStack.EMPTY);
		ItemStack remainder = insertItem(slot, stack, true);
		setStackInSlot(slot, currentStack);
		
		return remainder.isEmpty() || remainder.getCount() < stack.getCount();
	}
}