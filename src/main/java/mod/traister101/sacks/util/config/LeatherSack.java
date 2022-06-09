package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public final class LeatherSack {
	
	private static final String LANG_KEY = "config." + MODID + ".leather_sack";
	
	@RequiresMcRestart
	@Comment("Enable or disable this sack type")
	@LangKey(LANG_KEY + ".enabled")
	public boolean isEnabled = true;
	
	@Comment("Determins if this sack will automatically pickup items")
	@LangKey(LANG_KEY + ".pickup")
	public boolean doPickup = true;
	
	@RequiresWorldRestart
	@Comment("Item stack max for the type of sack")
	@LangKey(LANG_KEY + ".slot_cap")
	@RangeInt(min = 1, max = 512)
	public int slotCap = 64;
	
	@LangKey(LANG_KEY + ".size")
	@Comment("TFC does this kinda weird so it's actually one size lower i.e. this by default is anything smaller than normal")
	public Size allowedSize = Size.NORMAL;
}