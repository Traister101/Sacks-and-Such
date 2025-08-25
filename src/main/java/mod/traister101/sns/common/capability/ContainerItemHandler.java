package mod.traister101.sns.common.capability;

import mod.traister101.esc.common.capability.ExtendedSlotCapacityHandler;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.common.capabilities.size.*;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.ItemHandlerHelper;

public class ContainerItemHandler extends ExtendedSlotCapacityHandler {

	public final ContainerType type;
	private final ItemStack owner;

	public ContainerItemHandler(final ContainerType type, final ItemStack owner) {
		super(type.slotCount(), type.slotCapacity());
		this.type = type;
		this.owner = owner;
	}

	@Override
	public ItemStack insertItem(final int slotIndex, final ItemStack insertStack, final boolean simulate) {
		final ItemStack remainder = super.insertItem(slotIndex, insertStack, simulate);

		if (remainder.isEmpty()) return ItemStack.EMPTY;

		if (!SNSConfig.SERVER.doVoiding.get()) return remainder;

		if (!type.doesVoiding()) return remainder;

		if (owner.getCapability(SNSCapabilities.ITEM_VOIDER).map(itemVoider -> itemVoider.shouldSlotVoid(slotIndex)).orElse(false)) {
			if (ItemHandlerHelper.canItemStacksStack(insertStack, getStackInSlot(slotIndex))) return ItemStack.EMPTY;
		}

		return remainder;
	}

	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		if (itemStack.is(type.preventedItems())) return false;

		return type.allowedItems().map(itemStack::is).orElse(true) && fitsInSlot(itemStack);
	}

	@Override
	protected void onContentsChanged(final int slotIndex) {
		// Invalidate our cached weight when any contents change
		owner.getCapability(SNSCapabilities.DYNAMIC_WEIGHT).ifPresent(DynamicWeight::invalidate);
		super.onContentsChanged(slotIndex);
	}

	/**
	 * @param itemStack The {@link ItemStack} to check
	 *
	 * @return If the provided {@link ItemStack} will fit inside our slots
	 */
	protected final boolean fitsInSlot(final ItemStack itemStack) {
		final IItemSize stackSize = ItemSizeManager.get(itemStack);
		final Size size = stackSize.getSize(itemStack);
		// Larger than the slot size
		return size.isEqualOrSmallerThan(type.allowedSize());
	}
}