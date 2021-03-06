package mod.traister101.sacks.client.gui;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonSack;
import mod.traister101.sacks.network.RenamePacket;
import mod.traister101.sacks.objects.container.AbstractContainerRenameable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class GuiRenameable extends AbstractGuiContainer {
	
	protected final AbstractContainerRenameable container;
	protected final InventoryPlayer playerInv;
	
	private GuiTextField nameField;
	private boolean renaming;
	private String name;
	
	protected GuiRenameable(Container container, InventoryPlayer playerInv, ResourceLocation background, ItemStack heldStack) {
		super(container, background, heldStack);
		this.container = (AbstractContainerRenameable) inventorySlots;
		this.name = heldStack.getDisplayName();
		this.playerInv = playerInv;
	}
	
	@Override
	public void initGui() {
		super.initGui();
        Keyboard.enableRepeatEvents(true);
		addButton(new GuiButtonSack(0, guiLeft + 155, guiTop + 6, 16, 16, "rename", background));
		nameField = new GuiTextField(0, fontRenderer, guiLeft + 8, guiTop + 6, 140, 15);
		nameField.setTextColor(-1);
		nameField.setDisabledTextColour(-1);
		nameField.setMaxStringLength(22);
		nameField.setEnableBackgroundDrawing(false);
		nameField.setText(name);
		nameField.setEnabled(false);
		renaming = false;
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		nameField.drawTextBox();
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
	
	// TODO Text formating (colors and stuff) from player input
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		switch (keyCode) {
		// Escape key
		case 1:
			// Text box is enabled when escape is pressed
			if (renaming) {
				setRenaming(false);
			} else {
				mc.player.closeScreen();
			}
			return;
		// Enter key
		case 28:
			// Text box is enabled when enter is pressed
			if (renaming) {
				setRenaming(false);
				renameItem();
			} else {
				mc.player.closeScreen();
			}
			return;
		// Not enter or escape
		default:
			// Not entered into text box
			if (!nameField.textboxKeyTyped(typedChar, keyCode)) {
				super.keyTyped(typedChar, keyCode);
			}
		}
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
			nameField.setEnableBackgroundDrawing(true);
		} else {
			renaming = false;
			nameField.setFocused(false);
			nameField.setEnabled(false);
			nameField.setEnableBackgroundDrawing(false);
		}
	}
	
	private final void renameItem() {
		String name = nameField.getText();
		
		if (StringUtils.equals(name, this.name)) return;
		SacksNSuch.getNetwork().sendToServer(new RenamePacket(name));
		if (StringUtils.isBlank(name)) {
			// Best way I've figured out to set the text box to the default stack name 
			ItemStack heldStack = container.getSlot(container.getItemIndex()).getStack();
			ItemStack itemStack = heldStack.copy();
			itemStack.clearCustomName();
			this.name = itemStack.getDisplayName();
			nameField.setText(this.name);
		}
	}
}