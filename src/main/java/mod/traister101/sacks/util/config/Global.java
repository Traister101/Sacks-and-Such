package mod.traister101.sacks.util.config;

import static mod.traister101.sacks.SacksNSuch.MODID;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public final class Global {
	
	private static final String LANG_KEY = "config." + MODID + ".global";
	
	@LangKey(LANG_KEY + ".all_pickup")
	@Comment("Enable auto pickup for other sack like items such as the TFC vessel. This may not always work as expected enable at your own discretion")
	public boolean allPickup = false;
	
	@RequiresMcRestart
	@LangKey(LANG_KEY + ".pickup")
	@Comment("A global toggle for auto pickup. This will not force enable for every type")
	public boolean doPickup = true;
	
	@LangKey(LANG_KEY + ".glint_toggle")
	@Comment("This swaps the enchant glint from showing when a sack is voiding to when it's on autopickup")
	public boolean voidGlint = true;
	
	@LangKey(LANG_KEY + ".all_pickup_ore")
	@Comment("This makes all sack types capable of holding ore")
	public boolean allAllowOre = false;
	
	@LangKey(LANG_KEY + "shift_toggles_void")
	@Comment("This determins wheather shift right click toggles item voiding or item pickup for sacks")
	public boolean shiftClickTogglesVoid = false;
}