package mod.traister101.sacks.objects.container;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.inventory.capability.AbstractHandler;
import mod.traister101.sacks.util.SNSUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ContainerSack extends AbstractContainerRenameable {

    public ContainerSack(InventoryPlayer playerInv, ItemStack heldStack) {
        super(playerInv, heldStack);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        final boolean toggle = ((AbstractHandler) handler).hasItems();
        SacksNSuch.getNetwork().sendToServer(new TogglePacket(toggle, SNSUtils.ToggleType.ITEMS));
        super.onContainerClosed(playerIn);
    }
}