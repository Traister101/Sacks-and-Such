package mod.traister101.sacks;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.SacksNSuch.NAME;

import mod.traister101.sacks.util.config.*;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = MODID)
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
	@LangKey(LANG_GENERAL)
	public static final class General {
		
		@Comment("Config for thatch sack")
		@LangKey(LANG_GENERAL + ".thatch_sack")
		public static final ThatchSack THATCHSACK = new ThatchSack();

		@Comment("Config for burlap sack")
		@LangKey(LANG_GENERAL + ".burlap_sack")
		public static final BurlapSack BURLAPSACK = new BurlapSack();

		@Comment("Config for leather sack")
		@LangKey(LANG_GENERAL + ".leather_sack")
		public static final LeatherSack LEATHERSACK = new LeatherSack();

		@Comment("Config for miners sack")
		@LangKey(LANG_GENERAL + ".miner_sack")
		public static final MinerSack MINERSACK = new MinerSack();
		
		@Comment("Explosive vessel config")
		@LangKey(LANG_GENERAL + ".explosive_vessel")
		public static final ExplosiveVessel EXPLOSIVE_VESSEL = new ExplosiveVessel();
	}
	
	@LangKey(LANG_GLOBAL)
	@Config(modid = MODID, type = Type.INSTANCE, name = NAME + " - Global")
	public static final class Global {
		
		@Comment("Enable auto pickup for other sack like items such as the TFC vessel. This may not always work as expected enable at your own discretion")
		@LangKey(LANG_GLOBAL + ".all_pickup")
		public static boolean allPickup = false;
		
		@RequiresMcRestart
		@Comment("A global toggle for auto pickup. This will not force enable for every type")
		@LangKey(LANG_GLOBAL + ".pickup")
		public static boolean pickup = true;
	}
}