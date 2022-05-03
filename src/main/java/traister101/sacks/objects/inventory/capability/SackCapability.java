package traister101.sacks.objects.inventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import traister101.sacks.util.SackType;

public class SackCapability extends ItemStackHandler implements ICapabilityProvider, ISlotCallback {

	private final SackType type;
	private final int stacklimit;

	public SackCapability(@Nullable NBTTagCompound nbt, SackType type, int stacklimit) {
		super(SackType.getSlotsForType(type));
		this.type = type;
		this.stacklimit = stacklimit;
		if (nbt != null) {
			deserializeNBT(nbt);
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return hasCapability(capability, facing) ? (T) this : null;
	}

	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		IItemSize size = CapabilityItemSize.getIItemSize(stack);
		if (size != null) {
			return size.getSize(stack).isSmallerThan(Size.NORMAL);
		}
		return false;
	}

	@Override
	public int getSlotLimit(int slot) {
		return stacklimit;
	}
	
	@Override
	public int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return stacklimit;
	}
}