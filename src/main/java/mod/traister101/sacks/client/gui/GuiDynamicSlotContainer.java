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

/**
 * This Gui dynamically draws slots to the screen using the held Container's slots and their positions.
 *
 * The slot texture is {@link GuiDynamicSlotContainer#SLOT}. If you want your slot to have a graphic
 * inside similar to the ones seen in the player armor slots set a background name when creating the
 * slot via {@link Slot#setBackgroundName(String)} or override {@link Slot#getSlotTexture()}
 */
@SideOnly(Side.CLIENT)
public class GuiDynamicSlotContainer extends GuiContainer {

	private static final ResourceLocation SLOT = new ResourceLocation(SacksNSuch.MODID, "textures/gui/slot.png");
	protected final ResourceLocation background;
	protected final ItemStack heldStack;

	protected GuiDynamicSlotContainer(final Container container, final ResourceLocation background, final ItemStack heldStack) {
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
		for (final GuiButton button : buttonList) {
			if (button instanceof IButtonTooltip && button.isMouseOver()) {
				final IButtonTooltip tooltip = (IButtonTooltip) button;
				if (tooltip.hasTooltip()) {
					drawHoveringText(I18n.format(tooltip.getTooltip()), mouseX, mouseY);
				}

				final int x = button.x;
				final int y = button.y;
				drawRect(x, y, x + button.width, y + button.height, 0x75FFFFFF);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawSlots();
	}


	/**
	 * Dynamically draws the slot texture to the screen where slots are located.
	 */
	private void drawSlots() {
		mc.getTextureManager().bindTexture(SLOT);

		// Size of the texture which is a 18x18 pixel square (the size of the slot graphic)
		final int size = 18;

		// TODO come up with good idea to restrict drawn slots? Don't want to require our own slot extention
		// Yes we draw every slot, even the player inventory ones we have baked into the texture
		// Despite this performace is not a concern. Drawing of the ItemStacks the slots contain is much more expensive
		for (final Slot slot : inventorySlots.inventorySlots) {
			final int x = guiLeft + slot.xPos - 1;
			final int y = guiTop + slot.yPos - 1;
			drawModalRectWithCustomSizedTexture(x, y, size, size, size, size, size, size);
		}
	}
}