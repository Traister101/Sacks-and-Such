package mod.traister101.util.handlers;

import mod.traister101.Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler 
{
	 public static void openGui(World world, EntityPlayer player, Type type)
	    {
	        player.openGui(Main.instance, type.ordinal(), world, 0, 0, 0);
	    }
	
	 public enum Type
	    {
		 SACK_THATCH
	    }
	
}
