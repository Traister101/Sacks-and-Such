package mod.traister101.sns.common.capability;

import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;
import java.util.function.IntConsumer;

public class SimpleItemVoider implements ItemVoider, INBTSerializable<CompoundTag> {

	private final BitSet voidSlots = new BitSet();

	@Override
	public void forEachVoidSlot(final IntConsumer consumer) {
		voidSlots.stream().forEach(consumer);
	}

	@Override
	public boolean shouldSlotVoid(final int slotIndex) {
		return voidSlots.get(slotIndex);
	}

	@Override
	public boolean isVoidingEnabled() {
		return !voidSlots.isEmpty();
	}

	@Override
	public void toggleVoidSlot(final int slotIndex) {
		voidSlots.flip(slotIndex);
	}

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag compoundTag = new CompoundTag();
		compoundTag.putIntArray("voidSlots", voidSlots.stream().toArray());
		return compoundTag;
	}

	@Override
	public void deserializeNBT(final CompoundTag compoundTag) {
		voidSlots.clear();
		Arrays.stream(compoundTag.getIntArray("voidSlots")).forEach(voidSlots::set);
	}
}