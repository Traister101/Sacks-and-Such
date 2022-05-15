package mod.traister101.sacks.objects.inventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class SackHandler extends ItemStackHandler implements ICapabilityProvider, ISlotCallback {
	
	private final SackType type;
	private final int stacklimit;
	private final ItemStack sack;
	
	public SackHandler(@Nullable NBTTagCompound nbt, SackType type, ItemStack sack) {
		super(SackType.getSlotCount(type));
		this.type = type;
		this.stacklimit = SackType.getStackCap(type);
		this.sack = sack;
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
	
	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		
		if (amount == 0) return ItemStack.EMPTY;
		
		validateSlotIndex(slot);
		ItemStack existing = this.stacks.get(slot);
		if (existing.isEmpty()) return ItemStack.EMPTY;
		
		int toExtract = Math.min(amount, stacklimit);
		
		if (existing.getMaxStackSize() == 1) toExtract = 1;
		
		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				this.stacks.set(slot, ItemStack.EMPTY);
				onContentsChanged(slot);
			}
			return existing;
		} else {
			if (!simulate) {
				this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				onContentsChanged(slot);
			}
			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}
	
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		IItemSize size = CapabilityItemSize.getIItemSize(stack);
		if (size != null) {
			return size.getSize(stack).isSmallerThan(Size.NORMAL);
		}
		return false;
	}
	
	@Override
	public void onContentsChanged(int slot) {
		
	}
	
	@Override
	public int getSlotLimit(int slot) {
		return stacklimit;
	}
	
	@Override
	public int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return stacklimit;
	}
	
	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		super.setStackInSlot(slot, stack);
	}
	
	public NonNullList<ItemStack> getContents() {
		return stacks;
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList nbtTagList = new NBTTagList();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				int realCount = Math.min(stacklimit, stacks.get(i).getCount());
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				stacks.get(i).writeToNBT(itemTag);
				itemTag.setInteger("ExtendedCount", realCount);
				nbtTagList.appendTag(itemTag);
			}
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Items", nbtTagList);
		nbt.setInteger("Size", stacks.size());
		return nbt;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
		NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
			int slot = itemTags.getInteger("Slot");
			
			if (slot >= 0 && slot < stacks.size()) {
				if (itemTags.hasKey("StackList", Constants.NBT.TAG_LIST)) { // migrate from old ExtendedItemStack system
					ItemStack stack = ItemStack.EMPTY;
					NBTTagList stackTagList = itemTags.getTagList("StackList", Constants.NBT.TAG_COMPOUND);
					for (int j = 0; j < stackTagList.tagCount(); j++) {
						NBTTagCompound itemTag = stackTagList.getCompoundTagAt(j);
						ItemStack temp = new ItemStack(itemTag);
						if (!temp.isEmpty()) {
							if (stack.isEmpty())
								stack = temp;
							else
								stack.grow(temp.getCount());
						}
					}
					if (!stack.isEmpty()) {
						int count = stack.getCount();
						count = Math.min(count, getStackLimit(slot, stack));
						stack.setCount(count);
						
						stacks.set(slot, stack);
					}
				} else {
					ItemStack stack = new ItemStack(itemTags);
					if (itemTags.hasKey("ExtendedCount", Constants.NBT.TAG_INT)) {
						stack.setCount(itemTags.getInteger("ExtendedCount"));
					}
					stacks.set(slot, stack);
				}
			}
		}
		onLoad();
	}
}