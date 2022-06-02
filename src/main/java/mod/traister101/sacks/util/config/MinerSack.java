package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public final class MinerSack {
	
	private static final String LANGKEY = "config." + MODID + ".general.miner_sack";
	
	@RequiresMcRestart
	@Comment("Enable or disable this sack type")
	@LangKey(LANGKEY + ".enabled")
	public boolean enabled = true;

	@Comment("Determins if this sack will automatically pickup items")
	@LangKey(LANGKEY + ".pickup")
	public boolean pickup = true;
	
	@Comment("Allow non ore items inside the miner sack")
	@LangKey(LANGKEY + ".allow_non_ore")
	public boolean allowNonOre = false;
	
	@RequiresWorldRestart
	@Comment("Item stack max for the type of sack")
	@LangKey(LANGKEY + ".slot_cap")
	@RangeInt(min = 1, max = 512)
	public int slotCap = 512;
	
	@Comment("TFC does this kinda weird so it's actually one size lower ie this by default is anything smaller than large")
	@LangKey(LANGKEY + ".allowed_size")
	public String allowedSize = "large";
}