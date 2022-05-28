package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ExplosiveVessel {

	private static final String LANGKEY = "config." + MODID + ".general.explosive_vessel";
	
	@RequiresMcRestart
	@Comment("Enable or disable the explosive vessel")
	@LangKey(LANGKEY + ".enabled")
	public boolean enabled = true;
	
}