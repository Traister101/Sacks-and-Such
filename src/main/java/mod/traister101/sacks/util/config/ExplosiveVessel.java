package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.SlidingOption;

public class ExplosiveVessel {

	private static final String LANGKEY = "config." + MODID + ".general.explosive_vessel";
	
	@RequiresMcRestart
	@Comment("Enable or disable the explosive vessel")
	@LangKey(LANGKEY + ".enabled")
	public boolean enabled = true;
	
	@RequiresMcRestart
	@Comment("Enable or disable sticky explosive vessel")
	@LangKey(LANGKEY + ".stickyEnabled")
	public boolean stickyEnabled = true;
	
	@SlidingOption
	@RangeDouble(min = .1, max = 10)
	@Comment("Multiplier for explosion power" + "Use high values with caution the multiplier is applied last")
	@LangKey(LANGKEY + ".multiplier")
	public double explosionMultiplier = 1;
}