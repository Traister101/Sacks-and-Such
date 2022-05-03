package mod.traister101.sacks.objects.inventory.slot;

import javax.annotation.Nonnull;

import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SackSlot extends SlotItemHandler {
	
	private final ISlotCallback callback;
	
	public SackSlot(@Nonnull IItemHandler inventory, int idx, int x, int y, @Nonnull ISlotCallback callback) {
        super(inventory, idx, x, y);
        this.callback = callback;
    }
	
	@Override
	public int getSlotStackLimit() {
		return Math.min(callback.getSlotLimit(getSlotIndex()), super.getSlotStackLimit());
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return callback.getSlotLimit(slotNumber);
	}
}