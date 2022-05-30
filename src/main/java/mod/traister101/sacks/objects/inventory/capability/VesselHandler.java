package mod.traister101.sacks.objects.inventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.util.VesselType;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class VesselHandler extends AbstractHandler {
	
	private final VesselType type;
	
	public VesselHandler(@Nullable NBTTagCompound nbt, VesselType type) {
		super(nbt, 1, 256);
		this.type = type;
	}
	
	// TODO currently it just accepts gunpowder as the only vessel type is explosive but more types are planned such as a fire bomb
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if (OreDictionaryHelper.doesStackMatchOre(stack, "gunpowder")) return true;
		
		return false;
	}
}