package mod.traister101.sns.common.capability;

import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.ContainerType;
import mod.trasiter101.esc.common.capability.ExtendedSlotCapacityHandler;
import net.dries007.tfc.common.capabilities.size.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.IntConsumer;

public class ContainerItemHandler extends ExtendedSlotCapacityHandler implements IVoidingItemHandler {

	public final ContainerType type;
	protected final ItemStack handlerStack;
	private final Set<Integer> voidSlots = new HashSet<>();
	@Nullable
	private Weight cachedWeight;

	public ContainerItemHandler(final ContainerType type, final ItemStack handlerStack) {
		super(type.getSlotCount(), type.getSlotCapacity());
		this.type = type;
		this.handlerStack = handlerStack;
	}

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag compoundTag = super.serializeNBT();
		compoundTag.putByte("weight", (byte) (cachedWeight != null ? cachedWeight.ordinal() : -1));
		compoundTag.putIntArray("voidSlots", voidSlots.stream().mapToInt(Integer::intValue).toArray());
		return compoundTag;
	}

	@Override
	public void deserializeNBT(final CompoundTag compoundTag) {
		super.deserializeNBT(compoundTag);
		final byte weight = compoundTag.getByte("weight");
		if (weight != -1) cachedWeight = Weight.valueOf(weight);
		voidSlots.clear();
		voidSlots.addAll(Arrays.stream(compoundTag.getIntArray("voidSlots")).boxed().toList());
	}

	@Override
	public ItemStack insertItem(final int slotIndex, final ItemStack insertStack, final boolean simulate) {
		final ItemStack remainder = super.insertItem(slotIndex, insertStack, simulate);

		if (remainder.isEmpty()) return ItemStack.EMPTY;

		if (!SNSConfig.SERVER.doVoiding.get()) return remainder;

		if (!type.doesVoiding()) return remainder;

		if (voidSlots.contains(slotIndex) && ItemHandlerHelper.canItemStacksStack(insertStack, getStackInSlot(slotIndex))) return ItemStack.EMPTY;

		return remainder;
	}

	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		if (itemStack.is(SNSItemTags.PREVENTED_IN_ITEM_CONTAINERS)) return false;

		return fitsInSlot(itemStack);
	}

	@Override
	protected void onContentsChanged(final int slotIndex) {
		// Invalidate our cached weight when any contents change
		this.cachedWeight = null;
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
		// Larger than the sacks slot size
		return size.isEqualOrSmallerThan(type.getAllowedSize());
	}

	/**
	 * @return The weight of the sack
	 */
	public Weight getWeight() {
		if (cachedWeight != null) return cachedWeight;

		int totalItems = 0, maxCapacity = 0;

		for (int slotIndex = 0; slotIndex < getSlots(); slotIndex++) {
			final ItemStack itemStack = stacks.get(slotIndex);
			totalItems += itemStack.getCount();
			maxCapacity += getStackLimit(slotIndex, itemStack);
		}

		final float amountFilled = (float) totalItems / (float) maxCapacity;

		// TODO Simple percentage based approuch, maybe not the best?
		if (0.80 <= amountFilled) {
			return cachedWeight = Weight.VERY_HEAVY;
		}

		if (0.60 <= amountFilled) {
			return cachedWeight = Weight.HEAVY;
		}

		if (0.40 <= amountFilled) {
			return cachedWeight = Weight.MEDIUM;
		}

		if (0.20 <= amountFilled) {
			return cachedWeight = Weight.LIGHT;
		}

		return cachedWeight = Weight.VERY_LIGHT;
	}

	@Override
	public void forEachVoidSlot(final IntConsumer consumer) {
		voidSlots.forEach(consumer::accept);
	}

	@Override
	public boolean isVoidingEnabled() {
		return !voidSlots.isEmpty();
	}

	@Override
	public void toggleVoidSlot(final int slotIndex) {
		if (!type.doesVoiding()) return;

		if (voidSlots.contains(slotIndex)) {
			voidSlots.remove(slotIndex);
		} else {
			voidSlots.add(slotIndex);
		}
	}
}