package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeInt;

public class ThatchSack {
	
	private static final String LANGKEY = "config." + MODID + ".general.thatch_sack";
	
	@Config.RequiresMcRestart
	@Config.Comment("Enable or disable this sack type")
	@Config.LangKey(LANGKEY + ".enabled")
	public boolean enabled = true;
	
	@Config.Comment("Determins if this sack will automatically pickup items")
	@Config.LangKey(LANGKEY + ".pickup")
	public boolean pickup = true;
	
	@Config.RequiresWorldRestart
	@Config.Comment("Item stack max for the type of sack")
	@Config.LangKey(LANGKEY + ".slot_cap")
	@RangeInt(min = 1, max = 512)
	public int slotCap = 32;
	
}