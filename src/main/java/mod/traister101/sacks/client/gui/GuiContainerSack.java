package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiVoidButton;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.items.ItemSack;
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
        if (((ItemSack) heldStack.getItem()).getType().doesVoiding()) {
            addButton(new GuiVoidButton(1, guiLeft + 130, guiTop + 6, 16, 16, heldStack));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        final SimpleNetworkWrapper network = SacksNSuch.getNetwork();
        if (button.id == 1) {
            if (button instanceof GuiVoidButton) {
                final GuiVoidButton buttonToggle = (GuiVoidButton) button;
                network.sendToServer(new TogglePacket(!buttonToggle.isVoid, ToggleType.VOID));
                buttonToggle.onClick();
            }
        } else {
            super.actionPerformed(button);
        }
    }
}