package mod.traister101.sacks.client;


import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.PickBlockPacket;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.handlers.PickBlockHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
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


    private static final KeyBinding TOGGLE_VOID = new KeyBinding("sns.key.void", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, NAME);
    private static final KeyBinding TOGGLE_PICKUP = new KeyBinding("sns.key.pickup", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, NAME);


    public static void init() {
        ClientRegistry.registerKeyBinding(TOGGLE_VOID);
        ClientRegistry.registerKeyBinding(TOGGLE_PICKUP);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyPress(InputEvent event) {
        if (TOGGLE_VOID.isPressed()) {
            SacksNSuch.getNetwork().sendToServer(new TogglePacket(false, SNSUtils.ToggleType.VOID));
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindPickBlock.isPressed()) {
            SacksNSuch.getNetwork().sendToServer(new PickBlockPacket());
            PickBlockHandler.handlePickBlock(Minecraft.getMinecraft().player);
        }
    }
}