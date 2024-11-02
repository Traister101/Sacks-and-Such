package mod.traister101.sns.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;

import net.minecraftforge.client.settings.KeyConflictContext;

import static mod.traister101.sns.SacksNSuch.NAME;

public final class SNSKeybinds {

	public static final KeyMapping TOGGLE_PICKUP = new KeyMapping("sns.key.pickup", KeyConflictContext.IN_GAME, Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
			NAME);

	public static final KeyMapping OPEN_ITEM_CONTAINER = new KeyMapping("sns.key.openItemContainer", KeyConflictContext.IN_GAME, Type.KEYSYM,
			GLFW.GLFW_KEY_B, NAME);
}