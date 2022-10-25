package mod.traister101.sacks.util.config;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.common.config.Config.*;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class FarmerSack {

    private static final String LANG_KEY = "config." + MODID + ".farmer_sack.";

    @RequiresMcRestart
    @LangKey(LANG_KEY + "enabled")
    @Comment("Enable or disable this sack type")
    public boolean isEnabled = true;

    @RequiresMcRestart
    @LangKey(LANG_KEY + "pickup")
    @Comment("Determines if this sack will automatically pickup items")
    public boolean doPickup = true;

    @RequiresMcRestart
    @LangKey(LANG_KEY + "void")
    @Comment("Determines if this sack can void items on pickup")
    public boolean doVoiding = false;

    @LangKey(LANG_KEY + "allow_non_seed")
    @Comment("Allow non-seed items inside the farmer sack. ITEM SIZE IS STILL A CONSTRAINT")
    public boolean allowNonSeed = false;

    @RequiresMcRestart
    @RangeInt(min = 1, max = 64)
    @LangKey(LANG_KEY + "slot_cap")
    @Comment("Item stack max for the type of sack")
    public int slotCap = 64;

    @RequiresMcRestart
    @RangeInt(min = 1)
    @LangKey(LANG_KEY + "slot_count")
    @Comment("Honestly I recomend you not touch this it's meant for modpack devs who wanna get freaky with stuff")
    public int slotCount = 27;

    @RequiresMcRestart
    @LangKey(LANG_KEY + "size")
    @Comment("The maximum item size allowed in the sack")
    public Size allowedSize = Size.VERY_SMALL;
}