package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeInt;

public class ThatchSack {
	
	private static final String langKey = "config." + MODID + ".general.thatch_sack";

	@Config.RequiresMcRestart
	@Config.Comment("Enable or dissable sack")
	@Config.LangKey(langKey + ".toggle")
	public boolean enabled = true;
	
	@Config.RequiresWorldRestart
	@Config.Comment("Slot cap for sack")
	@Config.LangKey(langKey + ".slot_cap")
	@RangeInt(min = 1, max = 512)
	public int slotCap = 32;
	
}