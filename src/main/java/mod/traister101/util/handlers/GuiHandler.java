package mod.traister101.util.handlers;


import static mod.traister101.util.Reference.MODID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.Main;
import mod.traister101.client.gui.GuiContainerSack;
import mod.traister101.objects.container.ContainerSack;
import mod.traister101.objects.items.ItemSack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	public static final ResourceLocation THATCH_SACK = new ResourceLocation(MODID, "textures/gui/thatch_sack.png");
	public static final ResourceLocation LEATHER_SACK = new ResourceLocation(MODID, "textures/gui/leather_sack.png");
	
	public static void openGui(World world, EntityPlayer player, Type type)	{
		player.openGui(Main.getInstance(), type.ordinal(), world, 0, 0, 0);
	}
	
	@Override
	@Nullable
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)	{
		BlockPos pos = new BlockPos(x, y, z);
		ItemStack stack = player.getHeldItemMainhand();
		
		Type type = Type.valueOf(ID);
		switch (type) {
		case SACK_THATCH:
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case SACK_LEATHER:
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case SACK_BURLAP:
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		default:
			return null;
		}
	}
	
	@Override
	@Nullable
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		Container container = getServerGuiElement(ID, player, world, x, y, z);
		Type type = Type.valueOf(ID);
		BlockPos pos = new BlockPos(x, y, z);
		
		switch (type) {
		
		case SACK_THATCH:
			return new GuiContainerSack(container, player.inventory, THATCH_SACK);
		case SACK_LEATHER:
			return new GuiContainerSack(container, player.inventory, LEATHER_SACK);
		case SACK_BURLAP:
			return new GuiContainerSack(container, player.inventory, LEATHER_SACK);
		default:
			return null;
		}
	}
	
	public enum Type {
		
		SACK_THATCH,
		SACK_LEATHER,
		SACK_BURLAP,
		NULL;
		 
		private static final Type[] values = values();
		 
		@Nonnull
		public static Type valueOf(int id) {
			return id < 0 || id >= values.length ? NULL : values[id];
		}
	}
}
