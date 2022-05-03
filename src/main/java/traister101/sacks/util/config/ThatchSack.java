package traister101.sacks.util.config;

import static traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeInt;

// TODO Fix whatever is messed up with this that's causing the config file and the config in game to be formated weird
public class ThatchSack {
	
	private static final String langKey = "config." + MODID + ".general.thatch_sack";
	
	@Config.Comment("Enable or dissable sack")
	@Config.LangKey(langKey + ".toggle")
	public boolean thatchSack = true;

	@Config.Comment("Slot cap for sack")
	@Config.LangKey(langKey + ".slot_cap")
	@RangeInt(min = 1, max = 512)
	public int slotCap = 32;
	
}
