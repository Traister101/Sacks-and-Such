package traister101.sacks.util.config;

import static traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeInt;

public class ThatchSack {
	
	private static final String langKey = "config." + MODID + ".general.thatch_sack";
	
	@Config.LangKey(langKey + ".enable")
	public static boolean thatch_sack = true;
	
	@Config.LangKey(langKey + ".slots")
	@RangeInt(min = 1, max = 4)
	public static int slots = 4;
}