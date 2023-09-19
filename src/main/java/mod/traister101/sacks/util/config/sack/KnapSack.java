package mod.traister101.sacks.util.config.sack;

import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.common.config.Config.*;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class KnapSack {

	private static final String LANG_KEY = "config." + MODID + ".knap_sack.";

	@LangKey(LANG_KEY + "dangerous")
	@Comment("These configs are meant for modpack Devs though they should work fine in singleplayer")
	public final Dangerous DANGEROUS = new Dangerous();

	@RequiresWorldRestart
	@LangKey(LANG_KEY + "pickup")
	@Comment("Determines if this sack will automatically pickup items")
	public boolean doPickup = false;

	@RequiresWorldRestart
	@LangKey(LANG_KEY + "void")
	@Comment("Determines if this sack can void items on autopickup")
	public boolean doVoiding = false;

	@RequiresWorldRestart
	@RangeInt(min = 1)
	@LangKey(LANG_KEY + "slot_count")
	@Comment("This config has a realistic cap of 27 as any higher the slots are added on top of the player slots")
	public int slotCount = 18;

	@RequiresWorldRestart
	@RangeInt(min = 1, max = 64)
	@LangKey(LANG_KEY + "slot_cap")
	@Comment("Item stack max for the type of sack")
	public int slotCap = 64;

	@RequiresWorldRestart
	@LangKey(LANG_KEY + "size")
	@Comment("The maximum item size allowed in the sack")
	public Size allowedSize = Size.LARGE;

	public static class Dangerous {

		private static final String LANG_KEY = KnapSack.LANG_KEY + "dangerous.";

		@RequiresMcRestart
		@RangeInt(min = 1)
		@LangKey(LANG_KEY + "slot_count")
		@Comment("This config has a realistic cap of 27 as any higher the slots are added on top of the player slots")
		public int slotCount = 18;

		@RequiresMcRestart
		@LangKey(LANG_KEY + "enabled")
		@Comment("Enable or disable this sack type")
		public boolean isEnabled = true;
	}
}