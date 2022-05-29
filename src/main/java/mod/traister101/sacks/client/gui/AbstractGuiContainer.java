package mod.traister101.sacks.client.gui;

import net.dries007.tfc.client.button.IButtonTooltip;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractGuiContainer extends GuiContainer {

	protected final ResourceLocation background;
	protected final ItemStack heldStack;
	
	protected AbstractGuiContainer(Container container, ResourceLocation background, ItemStack heldStack) {
		super(container);
		this.background = background;
		this.heldStack = heldStack;
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
	}
	
	protected final void drawItemStack(ItemStack stack, int x, int y, String altText) {
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = fontRenderer;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}
	
	private void drawSimpleBackground() {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		super.renderHoveredToolTip(mouseX, mouseY);
		// Button Tooltips
		for (GuiButton button : buttonList) {
			if (button instanceof IButtonTooltip && button.isMouseOver()) {
				IButtonTooltip tooltip = (IButtonTooltip) button;
				if (tooltip.hasTooltip()) {
					drawHoveringText(I18n.format(tooltip.getTooltip()), mouseX, mouseY);
				}
				int x = button.x;
				int y = button.y;
				drawGradientRect(x, y, x + button.width, y + button.height, 0x75FFFFFF, 0x75FFFFFF);
			}
		}
	}
	
	protected void drawSlotOverlay(Slot slot) {
		int xPos = slot.xPos - 1;
		int yPos = slot.yPos - 1;

		drawGradientRect(xPos, yPos, xPos + 18, yPos + 18, 0x75FFFFFF, 0x75FFFFFF);
	}
}