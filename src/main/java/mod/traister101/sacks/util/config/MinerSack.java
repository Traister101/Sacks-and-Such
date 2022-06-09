package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public final class MinerSack {
	
	private static final String LANG_KEY = "config." + MODID + ".miner_sack";
	
	@RequiresMcRestart
	@LangKey(LANG_KEY + ".enabled")
	@Comment("Enable or disable this sack type")
	public boolean isEnabled = true;
	
	@LangKey(LANG_KEY + ".pickup")
	@Comment("Determins if this sack will automatically pickup items")
	public boolean doPickup = true;
	
	@LangKey(LANG_KEY + ".allow_non_ore")
	@Comment("Allow non ore items inside the miner sack. ITEM SIZE IS STILL A CONSTRAINT")
	public boolean allowNonOre = false;
	
	@RequiresWorldRestart
	@RangeInt(min = 1, max = 512)
	@LangKey(LANG_KEY + ".slot_cap")
	@Comment("Item stack max for the type of sack")
	public int slotCap = 512;
	
	@LangKey(LANG_KEY + ".size")
	@Comment("TFC does this kinda weird so it's actually one size lower i.e. this by default is anything smaller than large")
	public Size allowedSize = Size.LARGE;
}