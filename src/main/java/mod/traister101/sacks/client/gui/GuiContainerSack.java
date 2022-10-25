package mod.traister101.sacks.client.gui;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.button.GuiVoidButton;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.inventory.slot.AbstractSlot;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
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
            addButton(new GuiVoidButton(1, guiLeft + 130, guiTop + 6, 16, 16, heldStack));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        drawSlots();
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


    private void drawSlots() {
        mc.getTextureManager().bindTexture(new ResourceLocation(SacksNSuch.MODID, "textures/gui/slot.png"));
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
        GlStateManager.disableRescaleNormal();
        final int width = 18;
        final int height = 18;
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof AbstractSlot) {
                drawModalRectWithCustomSizedTexture(guiLeft + slot.xPos - 1, guiTop + slot.yPos - 1, width, height, width,  height, width, height);
            }
        }
    }
}