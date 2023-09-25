package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

@SideOnly(Side.CLIENT)
public abstract class AbstractGuiContainer extends GuiContainer {

    private static final ResourceLocation slot = new ResourceLocation(SacksNSuch.MODID, "textures/gui/slot.png");
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

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawSlots();
    }

    private void drawSlots() {
        mc.getTextureManager().bindTexture(slot);
        final int size = 18; // Size of the texture which is a square
        // The largest container we ship only renders 27 slots. That commeneded out loop makes it render
        // 27,000 and only cuts the framerate in half I don't think performance is a valid concern
//        for (int i = 0; i < 1000; i++)
        for (Slot slot : inventorySlots.inventorySlots) {
            // TODO fix this
            if (slot instanceof SlotItemHandler) {
                final int x = guiLeft + slot.xPos - 1;
                final int y = guiTop + slot.yPos - 1;
                drawModalRectWithCustomSizedTexture(x, y, size, size, size, size, size, size);
            }
        }
    }
}