package mod.traister101.sacks.client.gui;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import io.netty.buffer.Unpooled;
import mod.traister101.sacks.client.button.GuiButtonSack;
import mod.traister101.sacks.objects.container.ContainerSack;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO probably need to implement IContainerListener sack doesn't get properly renamed unless another container is opened before this one
@SideOnly(Side.CLIENT)
public class GuiContainerSack extends GuiContainer {
	
	protected final ResourceLocation background;
	protected final InventoryPlayer playerInv;
	private final ContainerSack container;
	protected ItemStack stack;
	private GuiTextField nameField;
	private boolean renaming;
	
	public GuiContainerSack(Container container, InventoryPlayer playerInv, ResourceLocation background, ItemStack stack) {
		super(container);
		this.container = (ContainerSack) inventorySlots;
		this.background = background;
		this.playerInv = playerInv;
		this.stack = stack;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		addButton(new GuiButtonSack(0, guiLeft + 151, guiTop + 4, 15, 15, "rename"));
		addButton(new GuiButtonSack(1, guiLeft + 20, guiTop + 60, 18, 18, "filter"));
		nameField = new GuiTextField(0, fontRenderer, guiLeft + 8, guiTop + 6, 150, 15);
		nameField.setTextColor(-1);
		nameField.setDisabledTextColour(-1);
		nameField.setMaxStringLength(25);
		nameField.setEnableBackgroundDrawing(false);
		nameField.setText(stack.getDisplayName());
		nameField.setEnabled(true);
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
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
		nameField.drawTextBox();
	}
	
	private void drawSimpleBackground() {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
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
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// Rename button
		if (button.id == 0) {
			// Toggle text box
			if (renaming) {
				setRename(false);
			} else setRename(true);
		}
		// Filter button when here
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		// Can't be focused if we aren't renaming
		if (!renaming) {
			super.keyTyped(typedChar, keyCode);
			return;
		}
		// Something typed in text box
		if (nameField.textboxKeyTyped(typedChar, keyCode)) {
			return;
		}
		// Enter key
		if (keyCode == 28) {
			// Field text is empty
			if (StringUtils.equals(nameField.getText(), "")) {
				super.keyTyped(typedChar, keyCode);
				mc.player.closeScreen();
				return;
			}
			// Field text has not changed
			if (StringUtils.equals(nameField.getText(), stack.getDisplayName())) {
				super.keyTyped(typedChar, keyCode);
				setRename(false);
				return;
			}
			renameItem();
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	// Toggles renaming and the text field
	private void setRename(boolean toggle) {
		if (toggle) {
			renaming = true;
			nameField.setEnabled(true);
			Keyboard.enableRepeatEvents(true);
			nameField.setEnableBackgroundDrawing(true);
			nameField.setFocused(true);
		} else {
			nameField.setEnabled(false);
			Keyboard.enableRepeatEvents(false);
			nameField.setEnableBackgroundDrawing(false);
			nameField.setFocused(false);
			renaming = false;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (renaming) nameField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	protected final void renameItem() {
		String name = nameField.getText();
		// TODO Probably right here is the problem code
		Slot slot = container.getSlot(container.getOpenContainerItemIndex());
		
		if (name == slot.getStack().getDisplayName()) return;
		
		container.updateItemName(name);
		mc.player.connection.sendPacket(new CPacketCustomPayload("MC|ItemName", (new PacketBuffer(Unpooled.buffer())).writeString(name)));
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