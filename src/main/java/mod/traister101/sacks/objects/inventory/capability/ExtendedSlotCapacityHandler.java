package mod.traister101.sacks.objects.inventory.capability;

import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * ItemHandler for extended stack sizes. Limited to {@link Integer#MAX_VALUE} as that's what {@link ItemStack} uses to store the
 * count internally. This is fine for our usage
 */
public class ExtendedSlotCapacityHandler extends ItemStackHandler implements ICapabilityProvider, ISlotCallback {

	protected final int slotStackLimit;

	protected ExtendedSlotCapacityHandler(final @Nullable NBTTagCompound nbt, final int slotCount, final int slotStackLimit) {
		super(slotCount);
		this.slotStackLimit = slotStackLimit;
		if (nbt != null) {
			deserializeNBT(nbt);
		}
	}

	@Override
	public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(final Capability<T> capability, @Nullable EnumFacing facing) {
		return hasCapability(capability, facing) ? (T) this : null;
	}

	@Override
	public int getSlotLimit(final int slotIndex) {
		return slotStackLimit;
	}

	@Override
	public int getStackLimit(final int slotIndex, final ItemStack itemStack) {
		return slotStackLimit;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagList nbtTagList = new NBTTagList();
		for (int slotIndex = 0; slotIndex < stacks.size(); slotIndex++) {
			final ItemStack slotStack = stacks.get(slotIndex);
			if (slotStack.isEmpty()) continue;

			final int realCount = Math.min(slotStackLimit, slotStack.getCount());
			final NBTTagCompound itemTag = new NBTTagCompound();
			itemTag.setInteger("Slot", slotIndex);
			slotStack.writeToNBT(itemTag);
			itemTag.setInteger("ExtendedCount", realCount);
			nbtTagList.appendTag(itemTag);
		}

		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Items", nbtTagList);
		return nbt;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound nbt) {
		final NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			final NBTTagCompound itemTag = tagList.getCompoundTagAt(i);

			final int slotIdex = itemTag.getInteger("Slot");

			if (0 > slotIdex || stacks.size() <= slotIdex) continue;

			final ItemStack itemStack = new ItemStack(itemTag);
			itemStack.setCount(itemTag.getInteger("ExtendedCount"));
			stacks.set(slotIdex, itemStack);
		}
		onLoad();
	}

	@Override
	public void onContentsChanged(final int slotIndex) {

	}
}