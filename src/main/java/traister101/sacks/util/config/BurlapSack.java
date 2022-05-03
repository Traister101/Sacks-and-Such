package traister101.sacks.util.config;

import static traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeInt;

public class BurlapSack {
	
	private static final String langKey = "config." + MODID + ".general.burlap_sack";

	@Config.Comment("Enable or dissable sack")
	@Config.LangKey(langKey + ".toggle")
	public boolean burlapSack = true;

	@Config.Comment("Slot cap for sack")
	@Config.LangKey(langKey + ".slot_cap")
	@RangeInt(min = 1, max = 512)
	public int slotCap = 48;
	
}
