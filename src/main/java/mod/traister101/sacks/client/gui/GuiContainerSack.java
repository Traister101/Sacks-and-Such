package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonSack;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.io.IOException;

public class GuiContainerSack extends GuiRenameable {

    public GuiContainerSack(Container container, InventoryPlayer playerInv, ResourceLocation background, ItemStack heldStack) {
        super(container, playerInv, background, heldStack);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (((ItemSack) heldStack.getItem()).getType().doesVoiding) {
            final String buttonText = SNSUtils.isAutoVoid(heldStack) ? "un-void" : "void";
            addButton(new GuiButtonSack(1, guiLeft + 130, guiTop + 6, 16, 16, buttonText));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        final SimpleNetworkWrapper network = SacksNSuch.getNetwork();
        if (button.id == 1) {
            network.sendToServer(new TogglePacket(!SNSUtils.isAutoVoid(heldStack), ToggleType.VOID));
            mc.player.closeScreen();
        } else {
            super.actionPerformed(button);
        }
    }
}