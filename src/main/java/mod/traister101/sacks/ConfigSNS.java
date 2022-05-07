package mod.traister101.sacks;

import static mod.traister101.sacks.SacksNSuch.MODID;

import mod.traister101.sacks.util.config.*;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = MODID)
public class ConfigSNS {
	
	@SubscribeEvent
	public static void onConfigChangedEvent(OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID)) {
			ConfigManager.sync(MODID, Type.INSTANCE);
		}
	}
	
	@Config(modid = MODID, category = "general", name = "Sacks N Such")
	@Config.LangKey("config." + MODID + ".general")
	public static final class General {
		
		@Config.Name("Thatch Sack")
		@Config.Comment("Thatch sack settings")
		@Config.LangKey("config." + MODID + ".general.thatch_sack")
		public static final ThatchSack THATCHSACK = new ThatchSack();

		@Config.Name("Leather Sack")
		@Config.Comment("Thatch sack settings")
		@Config.LangKey("config." + MODID + ".general.thatch_sack")
		public static final LeatherSack LEATHERSACK = new LeatherSack();

		@Config.Name("Burlap Sack")
		@Config.Comment("Burlap sack settings")
		@Config.LangKey("config." + MODID + ".general.burlap_sack")
		public static final BurlapSack BURLAPSACK = new BurlapSack();

		@Config.Name("Miners Sack")
		@Config.Comment("Miners sack settings")
		@Config.LangKey("config." + MODID + ".general.miner_sack")
		public static final MinerSack MINERSACK = new MinerSack();
	}
}