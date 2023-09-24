package mod.traister101.sacks.objects;

import mod.traister101.sacks.api.registries.SNSRegistryEvent.RegisterSackEvent;
import mod.traister101.sacks.api.types.SackType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static mod.traister101.sacks.ConfigSNS.SACK;
import static mod.traister101.sacks.SacksNSuch.MODID;

@EventBusSubscriber(modid = MODID)
public final class DefaultSacks {

	@SubscribeEvent
	public static void onSackHolderRegister(final RegisterSackEvent event) {
		final IForgeRegistry<SackType> registry = event.getRegistry();
		register(registry, "thatch_sack",
				new SackType(SACK.THATCH_SACK.slotCount, SACK.THATCH_SACK.slotCap, SACK.THATCH_SACK.doPickup, SACK.THATCH_SACK.doVoiding,
						SACK.THATCH_SACK.allowedSize));
		register(registry, "leather_sack",
				new SackType(SACK.LEATHER_SACK.slotCount, SACK.LEATHER_SACK.slotCap, SACK.LEATHER_SACK.doPickup, SACK.LEATHER_SACK.doVoiding,
						SACK.LEATHER_SACK.allowedSize));
		register(registry, "burlap_sack",
				new SackType(SACK.BURLAP_SACK.slotCount, SACK.BURLAP_SACK.slotCap, SACK.BURLAP_SACK.doPickup, SACK.BURLAP_SACK.doVoiding,
						SACK.BURLAP_SACK.allowedSize));
		register(registry, "miner_sack",
				new SackType(SACK.MINER_SACK.slotCount, SACK.MINER_SACK.slotCap, SACK.MINER_SACK.doPickup, SACK.MINER_SACK.doVoiding,
						SACK.MINER_SACK.allowedSize));
		register(registry, "farmer_sack",
				new SackType(SACK.FARMER_SACK.slotCount, SACK.FARMER_SACK.slotCap, SACK.FARMER_SACK.doPickup, SACK.FARMER_SACK.doVoiding,
						SACK.FARMER_SACK.allowedSize));
		register(registry, "knapsack",
				new SackType(SACK.KNAP_SACK.slotCount, SACK.KNAP_SACK.slotCap, SACK.KNAP_SACK.doPickup, SACK.KNAP_SACK.doVoiding,
						SACK.KNAP_SACK.allowedSize));
	}

	private static <T extends SackType> void register(final IForgeRegistry<SackType> registry, final String name, final T sackType) {
		sackType.setRegistryName(MODID, name);
		registry.register(sackType);
	}
}