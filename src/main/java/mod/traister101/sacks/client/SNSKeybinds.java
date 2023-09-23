package mod.traister101.sacks.client;


import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.handlers.PickBlockHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.SacksNSuch.NAME;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT, modid = MODID)
public final class SNSKeybinds {

	public static final Minecraft mc = Minecraft.getMinecraft();

	private static final KeyBinding TOGGLE_VOID = new KeyBinding("sns.key.void", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, NAME);
	private static final KeyBinding TOGGLE_PICKUP = new KeyBinding("sns.key.pickup", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, NAME);

	public static void init() {
		ClientRegistry.registerKeyBinding(TOGGLE_VOID);
		ClientRegistry.registerKeyBinding(TOGGLE_PICKUP);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onKeyPress(final InputEvent event) {

		if (TOGGLE_VOID.isPressed()) {
			final ItemStack heldStack = mc.player.getHeldItemMainhand();
			SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoVoid(heldStack), SNSUtils.ToggleType.VOID);
		}

		if (TOGGLE_PICKUP.isPressed()) {
			final ItemStack heldStack = mc.player.getHeldItemMainhand();
			SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoPickup(heldStack), SNSUtils.ToggleType.PICKUP);
		}

		// Pick block keybind
		if (Minecraft.getMinecraft().gameSettings.keyBindPickBlock.isPressed()) {
			// If we should handle pickblock (Client)
			if (ConfigSNS.doPickBlock) {
				PickBlockHandler.onPickBlock(mc.player, mc.objectMouseOver);
			}
		}
	}
}