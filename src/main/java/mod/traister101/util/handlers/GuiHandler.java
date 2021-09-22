package mod.traister101.util.handlers;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import mod.traister101.Main;
import mod.traister101.objects.container.ContainerSack;
import mod.traister101.objects.items.ItemSack;
import net.dries007.tfc.objects.container.ContainerSmallVessel;
import net.dries007.tfc.objects.items.ceramics.ItemSmallVessel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	
	public static void openGui(World world, EntityPlayer player, Type type)
	{
		player.openGui(Main.getInstance(), type.ordinal(), world, 0, 0, 0);
	}
	
	@Override
	@Nullable
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		ItemStack stack = player.getHeldItemMainhand();
		Type type = Type.valueOf(ID);
		switch (type)
		{
		case SACK_LEATHER:
			System.out.println("This should be leather sack GUI");
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case SACK_THATCH:
			System.out.println("This should be thatch sack GUI");
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		default:
			return null;
		 }
		 
	 }
	
	@Override
	@Nullable
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		Container container = getServerGuiElement(ID, player, world, x, y, z);
		Type type = Type.valueOf(ID);
		BlockPos pos = new BlockPos(x, y, z);
		switch (type)
		{
		case SACK_LEATHER:
		case SACK_THATCH:
		default:
			return null;
		}
	}
	
	public enum Type

	{
		SACK_LEATHER,
		SACK_THATCH,
		NULL;
		 
		 
		private static final Type[] values = values();
		 
		@Nonnull
		public static Type valueOf(int id)
		{
			return id < 0 || id >= values.length ? NULL : values[id];
			}
	}
}
