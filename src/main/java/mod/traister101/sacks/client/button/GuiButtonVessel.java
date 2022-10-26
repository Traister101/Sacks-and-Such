package mod.traister101.sacks.client.button;

import net.dries007.tfc.client.button.IButtonTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static mod.traister101.sacks.SacksNSuch.MODID;

@SideOnly(Side.CLIENT)
public class GuiButtonVessel extends GuiButton implements IButtonTooltip {

    private final ResourceLocation background = new ResourceLocation(MODID, "textures/gui/button/seal");

    public GuiButtonVessel(int buttonId, int startX, int startY, int width, int hight) {
        super(buttonId, startX, startY, width, hight, "");
    }

    @Override
    public String getTooltip() {
        return MODID + ".gui_vessel.tooltip.seal";
    }

    @Override
    public boolean hasTooltip() {
        return true;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(background);
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            drawModalRectWithCustomSizedTexture(x - 2, y - 2, 236, 20, 20, 20, 20, 20);
            mouseDragged(mc, mouseX, mouseY);
        }
    }
}