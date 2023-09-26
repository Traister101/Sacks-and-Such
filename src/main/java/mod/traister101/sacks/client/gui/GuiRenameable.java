package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonSack;
import mod.traister101.sacks.network.RenameHeldItemPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiRenameable extends GuiDynamicSlotContainer {

	private GuiTextField renameField;
	private boolean renaming;

	protected GuiRenameable(final Container container, final ResourceLocation background, final ItemStack heldStack) {
		super(container, background, heldStack);
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		addButton(new GuiButtonSack(0, guiLeft + 152, guiTop + 6, 16, 16, "rename"));
		renameField = new GuiTextField(0, fontRenderer, guiLeft + 8, guiTop + 6, 120, 15);
		renameField.setTextColor(-1);
		renameField.setDisabledTextColour(-1);
		renameField.setMaxStringLength(20);
		renameField.setEnableBackgroundDrawing(false);
		renameField.setText(heldStack.getDisplayName());
		renameField.setEnabled(false);
		renaming = false;
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
		if (renaming) renameField.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	// TODO Text formating (colors and stuff) from player input
	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
		// TODO migrate to if statements?
		switch (keyCode) {
			case Keyboard.KEY_ESCAPE:
				// Text box is enabled when escape is pressed
				if (renaming) {
					setRenaming(false);
				} else {
					mc.player.closeScreen();
				}
				return;
			// Enter key
			case Keyboard.KEY_RETURN:
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
				if (!renameField.textboxKeyTyped(typedChar, keyCode)) {
					super.keyTyped(typedChar, keyCode);
				}
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		renameField.drawTextBox();
	}

	@Override
	protected void actionPerformed(final GuiButton button) throws IOException {
		// Rename button
		if (button.id == 0) {
			// Toggle text box
			setRenaming(!renaming);
		}
	}

	/** Sets renaming and the text field */
	private void setRenaming(final boolean rename) {
		renaming = rename;
		renameField.setFocused(rename);
		renameField.setEnabled(rename);
		renameField.setEnableBackgroundDrawing(rename);
		renameField.setCursorPositionEnd();
	}

	private void renameItem() {
		final String renameFieldText = renameField.getText();

		if (StringUtils.equals(renameFieldText, heldStack.getDisplayName())) return;

		{
			final RenameHeldItemPacket packet = new RenameHeldItemPacket(renameFieldText, heldStack == mc.player.getHeldItemMainhand());
			SacksNSuch.getNetwork().sendToServer(packet);
		}

		if (StringUtils.isBlank(renameFieldText)) {
			renameField.setText(heldStack.getItem().getItemStackDisplayName(heldStack));
		}
	}
}