package mod.traister101.util.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static mod.traister101.util.Reference.MODID;

import mod.traister101.util.config.BurlapSack;
import mod.traister101.util.config.LeatherSack;
import mod.traister101.util.config.ThatchSack;
public class ConfigHandler {
	
	@SubscribeEvent
	public void onConfigChangedEvent(OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID))
		{
			ConfigManager.sync(MODID, Type.INSTANCE);
		}
	}
	
	@Config(modid = MODID, category = "general", name = "Sacks N Such - General")
	@Config.LangKey("config." + MODID + ".general")
	public static final class General {
		
		@Config.Comment("Thatch sack settings")
		@Config.LangKey("config." + MODID + ".general.thatch_sack")
		public static final ThatchSack THATCHSACK = new ThatchSack();
		
		@Config.Comment("Thatch sack settings")
		@Config.LangKey("config." + MODID + ".general.thatch_sack")
		public static final LeatherSack LEATHERSACK = new LeatherSack();
		
		@Config.Comment("Burlap sack settings")
		@Config.LangKey("config." + MODID + ".general.burlap_sack")
		public static final BurlapSack BURLAPSACK = new BurlapSack();
	}
}
