package mod.traister101.sns.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import mod.traister101.sns.SacksNSuch;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;

import net.minecraftforge.client.settings.KeyConflictContext;

public final class SNSKeybinds {

	public static final String CATEGORY_SACKS_N_SUCH = SacksNSuch.MODID + ".key.categories.sacks_n_such";

	public static final KeyMapping TOGGLE_PICKUP = create("pickup", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_UNKNOWN);

	public static final KeyMapping OPEN_ITEM_CONTAINER = create("openItemContainer", KeyConflictContext.IN_GAME, InputConstants.KEY_B);

	private static KeyMapping create(final String keyName, final KeyConflictContext keyContext, final int keyCode) {
		return new KeyMapping(SacksNSuch.MODID + ".key." + keyName, keyContext, Type.KEYSYM, keyCode, CATEGORY_SACKS_N_SUCH);
	}
}