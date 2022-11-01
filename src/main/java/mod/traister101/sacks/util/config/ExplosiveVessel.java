package mod.traister101.sacks.util.config;

import net.minecraftforge.common.config.Config.*;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class ExplosiveVessel {

    private static final String LANG_KEY = "config." + MODID + ".explosive_vessel.";

    @LangKey(LANG_KEY + "dangerous")
    @Comment("These configs are aimed at modpack Devs. Server client mismach will cause issues!")
    public final Dangerous DANGEROUS = new Dangerous();

    @RequiresMcRestart
    @RangeInt(min = 1, max = 512)
    @LangKey(LANG_KEY + "slot_cap")
    @Comment("Item stack max for the type of sack")
    public int slotCap = 256;

    @SlidingOption
    @RangeDouble(min = .1, max = 10)
    @LangKey(LANG_KEY + "multiplier")
    @Comment("Multiplier for explosion power" + "Use high values with caution the multiplier is applied last")
    public double explosionMultiplier = 1;

    @SlidingOption
    @RangeDouble(min = 0, max = 5)
    @LangKey(LANG_KEY + "small_power")
    @Comment("The explosion power of the tiny vessel")
    public double smallPower = 2.5;

    @RangeInt(min = 1)
    @LangKey(LANG_KEY + "strength_item_cap")
    @Comment("Cap for the amount of items used in the strength calculation, more than what is set are voided")
    public int strengthItemCap = 256;

    public static class Dangerous {

        private static final String LANG_KEY = ExplosiveVessel.LANG_KEY + "dangerous.";

        @RequiresMcRestart
        @RangeInt(min = 1)
        @LangKey(LANG_KEY + "slot_count")
        @Comment("This config has a relistic cap of 27 as any higher the slots are added on top of the player slots")
        public int slotCount = 1;

        @RequiresMcRestart
        @LangKey(LANG_KEY + "enabled")
        @Comment("Enable or disable the explosive vessel")
        public boolean isEnabled = true;

        @RequiresMcRestart
        @LangKey(LANG_KEY + "sticky_enabled")
        @Comment("Enable or disable sticky explosive vessel")
        public boolean stickyEnabled = true;

        @RequiresMcRestart
        @LangKey(LANG_KEY + "small_enabled")
        @Comment("Enable or disable the tiny explosive vessels")
        public boolean smallEnabled = true;
    }
}