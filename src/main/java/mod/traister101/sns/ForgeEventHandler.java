package mod.traister101.sns;

import mod.traister101.sns.util.handlers.PickupHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ForgeEventHandler {

	public static void init() {
		final var eventBus = MinecraftForge.EVENT_BUS;

		eventBus.addListener(PickupHandler::onPickupItem);
		// We want to handle this last to ensure we don't trample on anybody else
		eventBus.addListener(EventPriority.LOWEST, PickupHandler::onGroundBlockInteract);
	}
}