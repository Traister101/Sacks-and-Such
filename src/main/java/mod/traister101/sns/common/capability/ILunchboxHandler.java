package mod.traister101.sns.common.capability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public interface ILunchboxHandler {

	/**
	 * @return The currently selected stack. <strong>DO NOT MODIFY</strong>
	 */
	ItemStack getSelectedStack();

	/**
	 * @param entity The entity
	 *
	 * @return The selected stacks food properties. Potentially null
	 */
	@Nullable
	default FoodProperties getSelectedFoodProperties(@Nullable LivingEntity entity) {
		return getSelectedStack().getFoodProperties(entity);
	}

	/**
	 * Cycles the selected slot {@link CycleDirection#FORWARD} or {@link CycleDirection#BACKWARD}
	 */
	void cycleSelected(final CycleDirection cycleDirection);

	/**
	 * @return The selected slot index
	 */
	int getSelectedSlot();

	/**
	 * Eat the currently selected slot
	 *
	 * @param itemStack The held stack
	 * @param level The level
	 * @param livingEntity The living entity which is consuming the selected stack
	 *
	 * @return The new held stack
	 */
	ItemStack consumeSelected(ItemStack itemStack, Level level, LivingEntity livingEntity);

	enum CycleDirection {
		FORWARD,
		BACKWARD
	}
}