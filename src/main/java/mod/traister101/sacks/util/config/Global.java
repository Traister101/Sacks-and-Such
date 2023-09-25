package mod.traister101.sacks.util.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class Global {

	private static final String LANG_KEY = "config." + MODID + ".global.";

	@RequiresMcRestart
	@LangKey(LANG_KEY + "pickup")
	@Comment("Global control for automatic pickup, this will not force enable for every type")
	public boolean doPickup = true;

	@LangKey(LANG_KEY + "void")
	@Comment("A global toggle for item voiding, this will not force enable for every type")
	public boolean doVoiding = true;

	@LangKey(LANG_KEY + "glint_toggle")
	@Comment("This swaps the enchant glint from showing when a sack is voiding to when it's on auto-pickup")
	public boolean voidGlint = true;

	@LangKey(LANG_KEY + "all_pickup")
	@Comment("Enable auto pickup for other sack like items such as the TFC vessel." +
			"This may not always work as expected enable at your own discretion")
	public boolean allPickup = false;

	@LangKey(LANG_KEY + "all_pick_block")
	@Comment("This allows other containers such as vessels to support the pick block search")
	public boolean allPickBlock = false;

	@LangKey(LANG_KEY + "all_allow_ore")
	@Comment("This makes all sack types capable of holding ore")
	public boolean allAllowOre = false;

	@LangKey(LANG_KEY + "all_allow_food")
	@Comment("This makes all sacks capable of holding food although they won't preserve it!")
	public boolean allAllowFood = false;

	@LangKey(LANG_KEY + "shift_toggles_void")
	@Comment("This determines whether shift right click toggles item voiding or item pickup for sacks")
	public boolean shiftClickTogglesVoid = false;
}