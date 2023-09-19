package mod.traister101.sacks;

import mod.traister101.sacks.util.config.ExplosiveVessel;
import mod.traister101.sacks.util.config.Global;
import mod.traister101.sacks.util.config.SackConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.SacksNSuch.NAME;

@EventBusSubscriber(modid = MODID)
@Config(modid = MODID, type = Type.INSTANCE, name = NAME)
public final class ConfigSNS {

	@Comment("Global config")
	@LangKey("config." + MODID + ".global")
	public static final Global GLOBAL = new Global();
	@Comment("Sack config")
	@LangKey("config." + MODID + ".sack_config")
	public static final SackConfig SACK = new SackConfig();
	@Comment("Explosive vessel config")
	@LangKey("config." + MODID + ".explosive_vessel")
	public static final ExplosiveVessel EXPLOSIVE_VESSEL = new ExplosiveVessel();
	@LangKey("config." + MODID + ".do_pick_block")
	public static boolean doPickBlock = true;

	@SubscribeEvent
	public static void onConfigChangedEvent(final OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID)) {
			ConfigManager.sync(MODID, Type.INSTANCE);
		}
	}
}