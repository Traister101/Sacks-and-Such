package mod.traister101.sacks.client.gui;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonSack;
import mod.traister101.sacks.network.RenamePacket;
import mod.traister101.sacks.objects.container.ContainerSack;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiRenameable extends GuiContainer {
	
	protected final ResourceLocation background;
	protected final InventoryPlayer playerInv;
	protected final ContainerSack container;
	
	private GuiTextField nameField;
	private boolean renaming;
	private boolean renamed;
	private String name;
	
	public GuiRenameable(Container container, InventoryPlayer playerInv, ResourceLocation background, ItemStack stack) {
		super(container);
		this.container = (ContainerSack) inventorySlots;
		this.background = background;
		this.playerInv = playerInv;
		this.name = stack.getDisplayName();
	}
	
	@Override
	public void initGui() {
		super.initGui();
        Keyboard.enableRepeatEvents(true);
		addButton(new GuiButtonSack(0, guiLeft + 151, guiTop + 4, 15, 15, "rename"));
		nameField = new GuiTextField(0, fontRenderer, guiLeft + 8, guiTop + 6, 150, 15);
		nameField.setTextColor(-1);
		nameField.setDisabledTextColour(-1);
		nameField.setMaxStringLength(25);
		nameField.setEnableBackgroundDrawing(false);
		nameField.setText(name);
		nameField.setEnabled(true);
		renaming = false;
		renamed = false;
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		nameField.drawTextBox();
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
				setRenaming(false);
			} else setRenaming(true);
		}
	}
	
	// TODO clean this up so enter and escape close the text box then if hit again close GUI. 
	// Figure out how to do TextFormatting as well
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		// Can't be focused if we aren't renaming
		if (!renaming) {
			super.keyTyped(typedChar, keyCode);
			return;
		}
		// Enter key
		if (keyCode == 28) {
			// Field text is empty
			if (StringUtils.equals(nameField.getText(), "")) {
				super.keyTyped(typedChar, keyCode);
				mc.player.closeScreen();
				renameItem();
				return;
			}
			// Name is the same
			if (StringUtils.equals(TextFormatting.RESET + nameField.getText(), container.getOpenContainerItemStack().getDisplayName())) {
				super.keyTyped(typedChar, keyCode);
				setRenaming(false);
				return;
			}
			renameItem();
			setRenaming(false);
		}
		nameField.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (renaming) nameField.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	// Toggles renaming and the text field
	private final void setRenaming(boolean rename) {
		if (rename) {
			renaming = true;
			nameField.setFocused(true);
			nameField.setEnabled(true);
			Keyboard.enableRepeatEvents(true);
			nameField.setEnableBackgroundDrawing(true);
		} else {
			renaming = false;
			nameField.setFocused(false);
			nameField.setEnabled(false);
			Keyboard.enableRepeatEvents(false);
			nameField.setEnableBackgroundDrawing(false);
		}
	}
	
	private final void renameItem() {
		String name = nameField.getText();
		if (name == this.name) return;
		
		SacksNSuch.getNetwork().sendToServer(new RenamePacket(name));
		renamed = true;
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
