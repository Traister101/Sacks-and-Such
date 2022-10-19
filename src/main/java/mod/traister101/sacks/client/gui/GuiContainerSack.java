package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiButtonSack;
import mod.traister101.sacks.network.TogglePacket;
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
        addButton(new GuiButtonSack(1, guiLeft + 130, guiTop + 6, 16, 16, SNSUtils.isAutoVoid(heldStack) ? "un-void" : "void", background));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        // TODO filter button as well as some way to filter the allowed items
        SimpleNetworkWrapper network = SacksNSuch.getNetwork();
        switch (button.id) {
            case 0:
                super.actionPerformed(button);
                break;
            case 1:
                network.sendToServer(new TogglePacket(!SNSUtils.isAutoVoid(heldStack), ToggleType.VOID));
                mc.player.closeScreen();
                break;
            default:
                break;
        }
    }
}