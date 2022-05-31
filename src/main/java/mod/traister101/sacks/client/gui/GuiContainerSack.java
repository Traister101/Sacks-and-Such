package mod.traister101.sacks.client.gui;

import java.io.IOException;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonSack;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.util.helper.Utils;
import mod.traister101.sacks.util.helper.Utils.ToggleType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class GuiContainerSack extends GuiRenameable {

	public GuiContainerSack(Container container, InventoryPlayer playerInv, ResourceLocation background, ItemStack heldStack) {
		super(container, playerInv, background, heldStack);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		addButton(new GuiButtonSack(1, guiLeft + 155, guiTop + 22, 16, 16, "filter", background));
		addButton(new GuiButtonSack(2, guiLeft + 155, guiTop + 38, 16, 16, "void", background));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// TODO filter button as well as some way to filter the allowed items
		SimpleNetworkWrapper network = SacksNSuch.getNetwork();
		switch (button.id) {
		case 0: super.actionPerformed(button);
		case 1:
			SacksNSuch.getLog().info("Filter button clicked");
//			network.sendToServer(new TogglePacket(allowUserInput, null));
			break;
		case 2:
			network.sendToServer(new TogglePacket(!Utils.isAutoVoid(heldStack), ToggleType.VOID));
			mc.player.closeScreen();
			break;
		default:
			break;
		}
	}
}