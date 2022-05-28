package mod.traister101.sacks.client.gui;

import java.io.IOException;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonSack;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerSack extends GuiRenameable {

	public GuiContainerSack(Container container, InventoryPlayer playerInv, ResourceLocation background, ItemStack stack) {
		super(container, playerInv, background, stack);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		addButton(new GuiButtonSack(1, guiLeft + 20, guiTop + 60, 18, 18, "filter", background));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawSimpleBackground();
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
	
	private void drawSimpleBackground() {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// Rename button
		super.actionPerformed(button);
		// TODO filter button as well as some way to filter the allowed items
		// Filter button when here
		if (button.id == 1) {
			SacksNSuch.getLog().info("Filter button clicked");
		}
	}
}