package mod.traister101.util.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static mod.traister101.util.Reference.MODID;
public class ConfigHandler
{
	 @SubscribeEvent
	    public void onConfigChangedEvent(OnConfigChangedEvent event)
	    {
	        if (event.getModID().equals(MODID))
	        {
	            ConfigManager.sync(MODID, Type.INSTANCE);
	        }
	    }

	    @LangKey("config_test.config.types")
	    @Config(modid = MODID, type = Type.INSTANCE, name = MODID + "_types")
	    public static class CONFIG_TYPES
	    {
	    	@Name("Enable thatch sack?")
	    	public static boolean thatch_sack = true;
	    }
}
