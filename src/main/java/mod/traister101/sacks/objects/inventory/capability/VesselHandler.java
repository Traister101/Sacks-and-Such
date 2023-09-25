package mod.traister101.sacks.objects.inventory.capability;

import mod.traister101.sacks.util.VesselType;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VesselHandler extends ExtendedSlotCapacityHandler {

	public VesselHandler(@Nullable NBTTagCompound nbt, VesselType type) {
		super(nbt, type.slots, type.stackCap);
	}

	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return OreDictionaryHelper.doesStackMatchOre(stack, "gunpowder");
	}
}