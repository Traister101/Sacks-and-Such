package mod.traister101.sacks.objects.inventory.slot;

import net.minecraft.inventory.Slot;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class SackSlot extends AbstractSlot {

    public SackSlot(@Nonnull IItemHandler inventory, int index, int xPox, int yPos) {
        super(inventory, index, xPox, yPos);
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof SackSlot && ((SackSlot) other).getItemHandler() == getItemHandler();
    }
}