package mod.traister101.sacks.objects.container;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.util.SNSUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerSack extends ContainerRenameable {

	public ContainerSack(final InventoryPlayer playerInv, final ItemStack heldStack) {
		super(playerInv, heldStack);
	}

	@Override
	public void onContainerClosed(final EntityPlayer player) {
		final boolean toggle = ((SackHandler) handler).hasItems();
		SacksNSuch.getNetwork().sendToServer(new TogglePacket(toggle, SNSUtils.ToggleType.ITEMS));
		super.onContainerClosed(player);
	}
}