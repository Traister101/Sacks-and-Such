package mod.traister101.sacks.client.button;


import static mod.traister101.sacks.SacksNSuch.MODID;

import javax.annotation.Nonnull;

import mod.traister101.sacks.util.handlers.GuiHandler;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonSack extends GuiButton implements IButtonTooltip {
    
	private final ResourceLocation background;
	private final String buttonText;
	
    public GuiButtonSack(int buttonId, int startX, int startY, int width, int hight, String buttonText, ResourceLocation background) {
        super(buttonId, startX, startY, width, hight, "");
        this.background = background;
        this.buttonText = buttonText;
    }
    
    @Override
    public String getTooltip() {
        return MODID + ".tooltip.sack." + buttonText;
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
            drawModalRectWithCustomSizedTexture(x, y, 236, 20, 20, 20, 256, 256);
            mouseDragged(mc, mouseX, mouseY);
        }
    }
}