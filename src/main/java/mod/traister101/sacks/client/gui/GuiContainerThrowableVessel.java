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

public class GuiContainerThrowableVessel extends AbstractGuiContainer {
	
	public GuiContainerThrowableVessel(Container container, InventoryPlayer playerInv, ResourceLocation background, ItemStack heldStack) {
		super(container, background, heldStack);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		addButton(new GuiButtonSack(0, guiLeft + 155, guiTop + 6, 15, 15, "seal", background));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		fontRenderer.drawString(heldStack.getDisplayName(), guiLeft + 6, guiTop + 4, 4210752);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			SacksNSuch.getNetwork().sendToServer(new TogglePacket(!Utils.isSealed(heldStack), ToggleType.SEAL));
			mc.player.closeScreen();
		}
	}
}