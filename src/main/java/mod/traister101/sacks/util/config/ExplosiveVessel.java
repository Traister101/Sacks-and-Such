package mod.traister101.sacks.util.config;

import net.minecraftforge.common.config.Config.*;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class ExplosiveVessel {

    private static final String LANG_KEY = "config." + MODID + ".explosive_vessel";

    @RequiresMcRestart
    @LangKey(LANG_KEY + ".enabled")
    @Comment("Enable or disable the explosive vessel")
    public boolean isEnabled = true;

    @RequiresMcRestart
    @LangKey(LANG_KEY + ".sticky")
    @Comment("Enable or disable sticky explosive vessel")
    public boolean stickyEnabled = true;

    @RequiresMcRestart
    @LangKey(LANG_KEY + ".small")
    @Comment("Enable or disable the tiny explosive vessels")
    public boolean smallEnabled = true;

    @SlidingOption
    @RangeDouble(min = .1, max = 10)
    @LangKey(LANG_KEY + ".multiplier")
    @Comment("Multiplier for explosion power" + "Use high values with caution the multiplier is applied last")
    public double explosionMultiplier = 1;

    @SlidingOption
    @RangeDouble(min = 0, max = 5)
    @LangKey(LANG_KEY + ".small_power")
    @Comment("The explosion power of the tiny vessel")
    public double smallPower = 2.5;
}