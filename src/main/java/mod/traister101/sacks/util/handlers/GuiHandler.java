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

    public static final ResourceLocation SACK_SLOTS_1 = new ResourceLocation(MODID, "textures/gui/sack_1.png");
    public static final ResourceLocation SACK_SLOTS_4 = new ResourceLocation(MODID, "textures/gui/sack_4.png");
    public static final ResourceLocation SACK_SLOTS_18 = new ResourceLocation(MODID, "textures/gui/sack_18.png");
    public static final ResourceLocation SACK_SLOTS_27 = new ResourceLocation(MODID, "textures/gui/sack_27.png");
    public static final ResourceLocation VESSEL_SLOTS_1 = new ResourceLocation(MODID, "textures/gui/vessel_1.png");

    public static void openGui(World world, EntityPlayer player, GuiType guiType) {
        player.openGui(SacksNSuch.getInstance(), guiType.ordinal(), world, 0, 0, 0);
    }

    @Override
    @Nullable
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack stack = player.getHeldItemMainhand();
        GuiType guiType = GuiType.valueOf(ID);
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
        Container container = getServerGuiElement(ID, player, world, x, y, z);
        ItemStack stack = player.getHeldItemMainhand();
        GuiType guiType = GuiType.valueOf(ID);

        switch (guiType) {
            case SACK_THATCH:
            case SACK_LEATHER:
            case SACK_BURLAP:
                return new GuiContainerSack(container, player.inventory, SACK_SLOTS_4, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
            case SACK_MINER:
                return new GuiContainerSack(container, player.inventory, SACK_SLOTS_1, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
            case SACK_KNAP:
                return new GuiContainerSack(container, player.inventory, SACK_SLOTS_18, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
            case SACK_FARMER:
                return new GuiContainerSack(container, player.inventory, SACK_SLOTS_27, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
            case VESSEL_EXPLOSIVE:
                return new GuiContainerThrowableVessel(container, player.inventory, VESSEL_SLOTS_1, stack.getItem() instanceof ItemThrowableVessel ? stack : player.getHeldItemOffhand());
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
