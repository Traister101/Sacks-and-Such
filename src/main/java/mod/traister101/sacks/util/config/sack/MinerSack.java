package mod.traister101.sacks.util.config.sack;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.common.config.Config.*;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class MinerSack {

    private static final String LANG_KEY = "config." + MODID + ".miner_sack.";

    @LangKey(LANG_KEY + "dangerous")
    @Comment("These configs are aimed at modpack Devs. Server client mismach will cause issues!")
    public final Dangerous DANGEROUS = new Dangerous();

    @RequiresMcRestart
    @LangKey(LANG_KEY + "pickup")
    @Comment("Determines if this sack will automatically pickup items")
    public boolean doPickup = true;

    @RequiresMcRestart
    @LangKey(LANG_KEY + "void")
    @Comment("Determines if this sack can void items on pickup")
    public boolean doVoiding = false;

    @RequiresMcRestart
    @LangKey(LANG_KEY + "allow_non_ore")
    @Comment("Allow non ore items inside the miner sack. ITEM SIZE IS STILL A CONSTRAINT")
    public boolean allowNonOre = false;

    @RequiresMcRestart
    @RangeInt(min = 1, max = 512)
    @LangKey(LANG_KEY + "slot_cap")
    @Comment("Item stack max for the type of sack")
    public int slotCap = 512;

    @RequiresMcRestart
    @LangKey(LANG_KEY + "size")
    @Comment("The maximum item size allowed in the sack")
    public Size allowedSize = Size.SMALL;

    public static class Dangerous {

        private static final String LANG_KEY = MinerSack.LANG_KEY + "dangerous.";

        @RequiresMcRestart
        @RangeInt(min = 1)
        @LangKey(LANG_KEY + "slot_count")
        @Comment("This config has a realistic cap of 27 as any higher the slots are added on top of the player slots")
        public int slotCount = 1;

        @RequiresMcRestart
        @LangKey(LANG_KEY + "enabled")
        @Comment("Enable or disable this sack type")
        public boolean isEnabled = true;
    }
}