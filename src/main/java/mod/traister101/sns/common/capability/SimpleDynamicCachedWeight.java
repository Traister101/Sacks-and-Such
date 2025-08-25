package mod.traister101.sns.common.capability;

import mod.traister101.sns.util.items.ItemSlot;
import net.dries007.tfc.common.capabilities.size.Weight;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SimpleDynamicCachedWeight implements DynamicWeight {

	private final ItemStack owner;
	private final Function<ItemStack, Weight> weightCalculator;
	@Nullable
	private Weight cachedWeight;

	public static Weight percentageWeight(final ItemStack itemStack) {
		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
			@Value
			class TotalAndMax {

				int totalItems;
				int maxCapacity;

				private double amountFilled() {
					return (double) totalItems / (double) maxCapacity;
				}
			}

			final var amountFilled = ItemSlot.stream(handler)
					.collect(Collectors.teeing(
							Collectors.mapping(ItemSlot::getStack, Collectors.mapping(ItemStack::getCount, Collectors.reducing(0, Integer::sum))),
							Collectors.mapping(ItemSlot::slotLimit, Collectors.reducing(0, Integer::sum)), TotalAndMax::new))
					.amountFilled();

			if (0.8 <= amountFilled) {
				return Weight.VERY_HEAVY;
			}

			if (0.6 <= amountFilled) {
				return Weight.HEAVY;
			}

			if (0.4 <= amountFilled) {
				return Weight.MEDIUM;
			}

			if (0.2 <= amountFilled) {
				return Weight.LIGHT;
			}

			return Weight.VERY_LIGHT;
		}).orElse(Weight.VERY_HEAVY);
	}

	@Override
	public void invalidate() {
		cachedWeight = null;
	}

	@Override
	public Weight getWeight() {
		if (cachedWeight != null) return cachedWeight;
		return cachedWeight = weightCalculator.apply(owner);
	}
}