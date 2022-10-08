package mod.traister101.sacks.objects.inventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SackHandler extends AbstractHandler {
	
	private final SackType type;
	
	public SackHandler(@Nullable NBTTagCompound nbt, @Nonnull SackType type) {
		super(nbt, SackType.getSlotCount(type), SackType.getStackCap(type));
		this.type = type; 
	}
	
	// TODO More sacks (like the food sack) will need to be handled
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if (stack.isEmpty()) return false;
		if (!stack.isStackable()) return false;
		// Stack is a sack, no sack-ception
		if (stack.getItem() instanceof ItemSack) return false;
		
		Item item = stack.getItem();
		boolean isInvalid;
		
		
		if (type == SackType.FARMER) {
			//Not a seed
			if (!(item instanceof ItemSeedsTFC)) return false;
		} else {
			//Is a seed
			if (item instanceof ItemFoodTFC) return false;
		}
		
		if (type == SackType.MINER) {
			//Allow other than ore
			if (!ConfigSNS.MINER_SACK.allowNonOre) {
				//If item is a TFC ore
				if (!(item instanceof ItemOreTFC || item instanceof ItemSmallOre)) return false;
			}
		}
		
		if (!ConfigSNS.GLOBAL.allAllowOre)
		if (type != SackType.MINER)
		if (item instanceof ItemOreTFC || item instanceof ItemSmallOre) return false;
		
		if (item instanceof IItemSize) {
			//Larger than the sacks slot size
			if (!((IItemSize) item).getSize(stack).isSmallerThan(SackType.getSlotSize(type))) return false;
		}
		
		ItemStack currentStack = getStackInSlot(slot);
		setStackInSlot(slot, ItemStack.EMPTY);
		ItemStack remainder = insertItem(slot, stack, true);
		setStackInSlot(slot, currentStack);
		
		return remainder.isEmpty() || remainder.getCount() < stack.getCount();
	}
}