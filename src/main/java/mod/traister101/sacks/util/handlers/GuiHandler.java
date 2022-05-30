package mod.traister101.sacks.util.handlers;

import static mod.traister101.sacks.SacksNSuch.MODID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final ResourceLocation SLOTS_1 = new ResourceLocation(MODID, "textures/gui/sack_1.png");
	public static final ResourceLocation SLOTS_4 = new ResourceLocation(MODID, "textures/gui/sack_4.png");

	public static void openGui(World world, EntityPlayer player, GuiType guiType) {
		player.openGui(SacksNSuch.getInstance(), guiType.ordinal(), world, 0, 0, 0);
	}

	@Override
	@Nullable
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		ItemStack stack = player.getHeldItemMainhand();
		GuiType guiType = GuiType.valueOf(ID);
		switch (guiType) {
		case SACK_THATCH:
		case SACK_LEATHER:
		case SACK_BURLAP:
		case SACK_MINER:
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
		BlockPos pos = new BlockPos(x, y, z);
		
		switch (guiType) {
		case SACK_THATCH:
		case SACK_LEATHER:
		case SACK_BURLAP:
			return new GuiContainerSack(container, player.inventory, SLOTS_4, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case SACK_MINER:
			return new GuiContainerSack(container, player.inventory, SLOTS_1, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case VESSEL_EXPLOSIVE:
			return new GuiContainerThrowableVessel(container, player.inventory, SLOTS_1, stack.getItem() instanceof ItemThrowableVessel ? stack : player.getHeldItemOffhand());
		default:
			return null;
		}
	}
	
	public enum GuiType {
		SACK_THATCH,
		SACK_LEATHER,
		SACK_BURLAP,
		SACK_MINER,
		VESSEL_EXPLOSIVE,
		NULL;
		
		private static final GuiType[] values = values();
		
		@Nonnull
		public static GuiType valueOf(int id) {
			return id < 0 || id >= values.length ? NULL : values[id];
		}
	}
}
