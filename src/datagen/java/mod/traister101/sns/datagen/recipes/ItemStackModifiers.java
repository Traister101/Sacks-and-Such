package mod.traister101.sns.datagen.recipes;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unused")
public final class ItemStackModifiers {

	public static final ItemStackModifier COPY_INPUT = ItemStackModifier.simple("tfc:copy_input");
	public static final ItemStackModifier COPY_FOOD = ItemStackModifier.simple("tfc:copy_food");
	public static final ItemStackModifier COPY_OLDEST_FOOD = ItemStackModifier.simple("tfc:copy_oldest_food");
	public static final ItemStackModifier COPY_HEAT = ItemStackModifier.simple("tfc:copy_heat");
	public static final ItemStackModifier COPY_FORGING_BONUS = ItemStackModifier.simple("tfc:copy_forging_bonus");
	public static final ItemStackModifier RESET_FOOD = ItemStackModifier.simple("tfc:reset_food");
	public static final ItemStackModifier EMPTY_BOWL = ItemStackModifier.simple("tfc:empty_bowl");
	public static final ItemStackModifier ADD_BAIT_TO_ROD = ItemStackModifier.simple("tfc:add_bait_to_rod");
	public static final ItemStackModifier ADD_GLASS = ItemStackModifier.simple("tfc:add_glass");
	public static final ItemStackModifier ADD_POWDER = ItemStackModifier.simple("tfc:add_powder");
}