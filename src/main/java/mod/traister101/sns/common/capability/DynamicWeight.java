package mod.traister101.sns.common.capability;

import net.dries007.tfc.common.capabilities.size.Weight;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.function.Function;

@AutoRegisterCapability
public interface DynamicWeight {

	static Function<ItemStack, Weight> weightFunction(final Weight fallback) {
		return itemStack -> itemStack.getCapability(SNSCapabilities.DYNAMIC_WEIGHT).map(DynamicWeight::getWeight).orElse(fallback);
	}

	void invalidate();

	Weight getWeight();
}