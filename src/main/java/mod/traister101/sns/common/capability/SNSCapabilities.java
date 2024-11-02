package mod.traister101.sns.common.capability;

import net.minecraftforge.common.capabilities.*;

public class SNSCapabilities {

	public static final Capability<ILunchboxHandler> LUNCHBOX = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final Capability<IVoidingItemHandler> ITEM_VOIDING_ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>() {
	});
}