package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiVoidButton;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiContainerSack extends GuiRenameable {

	public GuiContainerSack(final Container container, final ResourceLocation background, final ItemStack heldStack) {
		super(container, background, heldStack);
	}

	@Override
	public void initGui() {
		super.initGui();
		if (((ItemSack) heldStack.getItem()).getType().doesVoiding()) {
			addButton(new GuiVoidButton(1, guiLeft + 130, guiTop + 6, 16, 16, heldStack));
		}
	}

	@Override
	protected void actionPerformed(final GuiButton button) throws IOException {
		super.actionPerformed(button);

		if (button.id == 1) {
			if (!(button instanceof GuiVoidButton)) return;

			final GuiVoidButton voidButton = (GuiVoidButton) button;
			SacksNSuch.getNetwork().sendToServer(new TogglePacket(!voidButton.isVoid, ToggleType.VOID));
			voidButton.onClick();
		}
	}
}