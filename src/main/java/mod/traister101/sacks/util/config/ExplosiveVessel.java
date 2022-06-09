package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.SlidingOption;

public final class ExplosiveVessel {
	
	private static final String LANG_KEY = "config." + MODID + ".explosive_vessel";
	
	@RequiresMcRestart
	@LangKey(LANG_KEY + ".enabled")
	@Comment("Enable or disable the explosive vessel")
	public boolean enabled = true;
	
	@RequiresMcRestart
	@LangKey(LANG_KEY + ".sticky")
	@Comment("Enable or disable sticky explosive vessel")
	public boolean stickyEnabled = true;
	
	@SlidingOption
	@RangeDouble(min = .1, max = 10)
	@LangKey(LANG_KEY + ".multiplier")
	@Comment("Multiplier for explosion power" + "Use high values with caution the multiplier is applied last")
	public double explosionMultiplier = 1;
}