package mod.traister101.sacks.objects.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.inventory.Slot;
import net.minecraftforge.items.IItemHandler;

public class SackSlot extends AbstractSlot {
	
	public SackSlot(@Nonnull IItemHandler inventory, int index, int xPox, int yPos) {
        super(inventory, index, xPox, yPos);
    }
	
	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof SackSlot && ((SackSlot) other).getItemHandler() == handler;
	}
}