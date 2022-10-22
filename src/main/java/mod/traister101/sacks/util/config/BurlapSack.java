package mod.traister101.sacks.util.config;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.common.config.Config.*;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class BurlapSack {

    private static final String LANG_KEY = "config." + MODID + ".burlap_sack.";

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
    public boolean doVoiding = true;

    @RequiresMcRestart
    @RangeInt(min = 1, max = 512)
    @LangKey(LANG_KEY + "slot_cap")
    @Comment("Item stack max for the type of sack")
    public int slotCap = 48;

    @RequiresMcRestart
    @LangKey(LANG_KEY + "size")
    @Comment("The maximum item size allowed in the sack")
    public Size allowedSize = Size.SMALL;
}