package mod.traister101.sacks;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.SacksNSuch.NAME;

import mod.traister101.sacks.util.config.*;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = MODID)
@Config(modid = MODID, type = Type.INSTANCE, name = NAME)
public final class ConfigSNS {
	
	private static final String LANG_KEY = "config." + MODID;
	
	@SubscribeEvent
	public static void onConfigChangedEvent(OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID)) {
			ConfigManager.sync(MODID, Type.INSTANCE);
		}
	}
	
	@Comment("Config for thatch sack")
	@LangKey(LANG_KEY + ".thatch_sack")
	public static final ThatchSack THATCH_SACK = new ThatchSack();
	
	@Comment("Config for burlap sack")
	@LangKey(LANG_KEY + ".burlap_sack")
	public static final BurlapSack BURLAP_SACK = new BurlapSack();
	
	@Comment("Config for leather sack")
	@LangKey(LANG_KEY + ".leather_sack")
	public static final LeatherSack LEATHER_SACK = new LeatherSack();
	
	@Comment("Config for miners sack")
	@LangKey(LANG_KEY + ".miner_sack")
	public static final MinerSack MINER_SACK = new MinerSack();
	
	@Comment("Explosive vessel config")
	@LangKey(LANG_KEY + ".explosive_vessel")
	public static final ExplosiveVessel EXPLOSIVE_VESSEL = new ExplosiveVessel();
	
	@Comment("Global config")
	@LangKey(LANG_KEY + ".global")
	public static final Global GLOBAL = new Global();
}