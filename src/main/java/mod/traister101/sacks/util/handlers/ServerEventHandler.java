package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.api.SackRegistry;
import mod.traister101.sacks.api.types.SackType;
import mod.traister101.sacks.network.SackTypeSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber(modid = SacksNSuch.MODID)
public final class ServerEventHandler {

	@SubscribeEvent
	public static void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
		// Syncs the server sack registry values to the client ensuring our config controlled containers are safe
		for (final SackType sackType : SackRegistry.SACK_TYPES) {
			SacksNSuch.getNetwork().sendTo(new SackTypeSync(sackType), (EntityPlayerMP) event.player);
		}
	}
}