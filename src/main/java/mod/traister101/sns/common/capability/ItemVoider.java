package mod.traister101.sns.common.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.function.IntConsumer;

@AutoRegisterCapability
public interface ItemVoider {

	/**
	 * Run an action for every slot which is being voided
	 */
	void forEachVoidSlot(IntConsumer consumer);

	/**
	 * If any slots are currently enabled for voiding
	 */
	boolean isVoidingEnabled();

	/**
	 * If the given slot index should void items
	 */
	boolean shouldSlotVoid(int slotIndex);

	/**
	 * Toggles a void slot for the purposes of voiding items
	 *
	 * @param slotIndex The slot to toggle
	 */
	void toggleVoidSlot(int slotIndex);
}