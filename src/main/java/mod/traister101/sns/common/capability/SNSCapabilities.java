package mod.traister101.sns.common.capability;

import net.minecraftforge.common.capabilities.*;

public class SNSCapabilities {

	public static final Capability<FoodHolder> FOOD_HOLDER = CapabilityManager.get(new CapabilityToken<>() {});

	public static final Capability<ItemVoider> ITEM_VOIDER = CapabilityManager.get(new CapabilityToken<>() {});

	public static final Capability<DynamicWeight> DYNAMIC_WEIGHT = CapabilityManager.get(new CapabilityToken<>() {});
}