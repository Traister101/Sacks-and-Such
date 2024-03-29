package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonVessel;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiContainerThrowableVessel extends GuiDynamicSlotContainer {

	public GuiContainerThrowableVessel(final Container container, final ResourceLocation background, final ItemStack heldStack) {
		super(container, background, heldStack);
	}

	@Override
	public void initGui() {
		super.initGui();
		addButton(new GuiButtonVessel(0, guiLeft + 152, guiTop + 6, 15, 15));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		fontRenderer.drawString(heldStack.getDisplayName(), guiLeft + 6, guiTop + 4, 0xFFFFFF);
	}

	@Override
	protected void actionPerformed(final GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id == 0) {
			SacksNSuch.getNetwork().sendToServer(new TogglePacket(!NBTHelper.isSealed(heldStack), ToggleType.SEAL));
			mc.player.sendStatusMessage(new TextComponentTranslation(SacksNSuch.MODID + ".explosive_vessel.seal.enabled"), true);
			mc.player.closeScreen();
		}
	}
}