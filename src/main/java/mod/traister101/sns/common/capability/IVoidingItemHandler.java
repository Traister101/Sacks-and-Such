package mod.traister101.sns.common.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.items.IItemHandler;

import java.util.function.IntConsumer;

@AutoRegisterCapability
public interface IVoidingItemHandler extends IItemHandler {

	/**
	 * Run an action for every slot which is being voided
	 */
	void forEachVoidSlot(final IntConsumer consumer);

	/**
	 * If any slots are currently enabled for voiding
	 */
	boolean isVoidingEnabled();

	/**
	 * Toggles a void slot for the purposes of voiding items
	 *
	 * @param slotIndex The slot to toggle
	 */
	void toggleVoidSlot(int slotIndex);
}