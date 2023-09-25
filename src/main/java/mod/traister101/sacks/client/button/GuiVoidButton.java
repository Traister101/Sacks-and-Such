package mod.traister101.sacks.client.button;

import mod.traister101.sacks.util.NBTHelper;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static mod.traister101.sacks.SacksNSuch.MODID;

@SideOnly(Side.CLIENT)
public class GuiVoidButton extends GuiButton implements IButtonTooltip {

	private final ResourceLocation voidTexture = new ResourceLocation(MODID, "textures/gui/button/void.png");
	private final ResourceLocation unVoidTexture = new ResourceLocation(MODID, "textures/gui/button/un-void.png");
	public boolean isVoid;

	public GuiVoidButton(int buttonId, int x, int y, int widthIn, int heightIn, ItemStack stack) {
		super(buttonId, x, y, widthIn, heightIn, "");
		this.isVoid = NBTHelper.isAutoVoid(stack);
	}

	@Override
	public String getTooltip() {
		return MODID + ".gui_sack.tooltip." + (isVoid ? "un-void" : "void");
	}

	@Override
	public boolean hasTooltip() {
		return true;
	}

	public void onClick() {
		isVoid = !isVoid;
	}

	@Override
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			GlStateManager.color(1, 1, 1, 1);
			if (isVoid) {
				mc.getTextureManager().bindTexture(voidTexture);
			} else {
				mc.getTextureManager().bindTexture(unVoidTexture);
			}
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			drawModalRectWithCustomSizedTexture(x - 2, y - 2, 20, 20, 20, 20, 20, 20);
			mouseDragged(mc, mouseX, mouseY);
		}
	}
}
