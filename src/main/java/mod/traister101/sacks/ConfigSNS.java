package mod.traister101.sacks;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.SacksNSuch.NAME;

import mod.traister101.sacks.util.config.*;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = MODID)
public final class ConfigSNS {
	
	private static final String LANG_GENERAL = "config." + MODID + ".general";
	private static final String LANG_GLOBAL = "config." + MODID + ".global";
	
	@SubscribeEvent
	public static void onConfigChangedEvent(OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID)) {
			ConfigManager.sync(MODID, Type.INSTANCE);
		}
	}
	
	@Config(modid = MODID, type = Type.INSTANCE, name = NAME + " - General")
	@Config.LangKey(LANG_GENERAL)
	public static final class General {
		
		@Config.Comment("Config for thatch sack")
		@Config.LangKey(LANG_GENERAL + ".thatch_sack")
		public static final ThatchSack THATCHSACK = new ThatchSack();

		@Config.Comment("Config for burlap sack")
		@Config.LangKey(LANG_GENERAL + ".burlap_sack")
		public static final BurlapSack BURLAPSACK = new BurlapSack();

		@Config.Comment("Config for leather sack")
		@Config.LangKey(LANG_GENERAL + ".leather_sack")
		public static final LeatherSack LEATHERSACK = new LeatherSack();

		@Config.Comment("Config for miners sack")
		@Config.LangKey(LANG_GENERAL + ".miner_sack")
		public static final MinerSack MINERSACK = new MinerSack();
	}
	
	@Config.LangKey(LANG_GLOBAL)
	@Config(modid = MODID, type = Type.INSTANCE, name = NAME + " - Global")
	public static final class Global {
		
		@Config.Comment("Global toggle for all mod features \n Why not?")
		@Config.LangKey(LANG_GLOBAL + ".enabled")
		public static boolean enabled = true;
		
		@Config.RequiresMcRestart
		@Config.Comment("A global toggle for auto pickup. This will not force enable for every type")
		@Config.LangKey(LANG_GLOBAL + ".pickup")
		public static boolean pickup = true;
	}
}