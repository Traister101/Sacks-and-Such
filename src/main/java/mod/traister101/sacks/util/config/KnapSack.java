package mod.traister101.sacks.util.config;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.common.config.Config.*;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class KnapSack {

    private static final String LANG_KEY = "config." + MODID + ".knap_sack";

    @RequiresMcRestart
    @LangKey(LANG_KEY + ".enabled")
    @Comment("Enable or disable this sack type")
    public boolean isEnabled = true;

    @LangKey(LANG_KEY + ".pickup")
    @Comment("Determines if this sack will automatically pickup items")
    public boolean doPickup = true;

    @LangKey(LANG_KEY + ".void")
    @Comment("Determines if this sack can void items")
    public boolean doVoiding = true;

    @RequiresWorldRestart
    @RangeInt(min = 1, max = 64)
    @LangKey(LANG_KEY + ".slot_cap")
    @Comment("Item stack max for the type of sack")
    public int slotCap = 64;

    @LangKey(LANG_KEY + ".size")
    @Comment("TFC does this kinda weird so it's actually one size lower i.e. this by default is anything smaller than Very Large")
    public Size allowedSize = Size.VERY_LARGE;
}