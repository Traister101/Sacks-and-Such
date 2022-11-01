package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.gui.GuiContainerSack;
import mod.traister101.sacks.client.gui.GuiContainerThrowableVessel;
import mod.traister101.sacks.objects.container.ContainerSack;
import mod.traister101.sacks.objects.container.ContainerThrowableVessel;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class GuiHandler implements IGuiHandler {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(MODID, "textures/gui/plain.png");

    public static void openGui(World world, EntityPlayer player, GuiType guiType) {
        player.openGui(SacksNSuch.getInstance(), guiType.ordinal(), world, 0, 0, 0);
    }

    @Override
    @Nullable
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        final GuiType guiType = GuiType.valueOf(ID);
        ItemStack stack = player.getHeldItemMainhand();
        switch (guiType) {
            case SACK_THATCH:
            case SACK_LEATHER:
            case SACK_BURLAP:
            case SACK_MINER:
            case SACK_FARMER:
            case SACK_KNAP:
                return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
            case VESSEL_EXPLOSIVE:
                return new ContainerThrowableVessel(player.inventory, stack.getItem() instanceof ItemThrowableVessel ? stack : player.getHeldItemOffhand());
            default:
                return null;
        }
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        final Container container = getServerGuiElement(ID, player, world, x, y, z);
        final GuiType guiType = GuiType.valueOf(ID);
        ItemStack stack = player.getHeldItemMainhand();
        switch (guiType) {
            case SACK_THATCH:
            case SACK_LEATHER:
            case SACK_BURLAP:
            case SACK_MINER:
            case SACK_KNAP:
            case SACK_FARMER:
                // Yeah this seems like it'd cause issues with the variable slots but we render those dynamiclly rather than doing them ahead of time in the texture
                return new GuiContainerSack(container, player.inventory, BACKGROUND, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
            case VESSEL_EXPLOSIVE:
                return new GuiContainerThrowableVessel(container, BACKGROUND, stack.getItem() instanceof ItemThrowableVessel ? stack : player.getHeldItemOffhand());
            default:
                return null;
        }
    }

    public enum GuiType {
        SACK_THATCH,
        SACK_LEATHER,
        SACK_BURLAP,
        SACK_MINER,
        SACK_FARMER,
        VESSEL_EXPLOSIVE,
        SACK_KNAP,
        NULL;

        private static final GuiType[] values = values();

        @Nonnull
        public static GuiType valueOf(int id) {
            return id < 0 || id >= values.length ? NULL : values[id];
        }
    }
}
