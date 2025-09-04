package mod.traister101.sns.config.entries;

import mod.traister101.sns.common.items.HorseshoesItem.HorseshoesProperties;

import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

import lombok.*;
import lombok.experimental.*;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class HorseshoesConfig implements HorseshoesProperties {

	IntValue stepsPerDamage;
	DoubleValue movementSpeed;
	DoubleValue bonusFallDistance;
	DoubleValue bonusStepDistance;

	public static HorseshoesConfig build(final Builder builder, final String bootsName, final int stepsPerDamage, final double movementSpeed,
			final double bonusFallDistance, final double bonusStepDistance) {
		builder.push(bootsName);
		final HorseshoesConfig config = new HorseshoesConfig(builder.comment("The amount of 'steps' taken before one point of durability is lost",
						"Steps are defined as being any change in position while grounded between ticks (IE over 1 second 20 'steps' occur)")
				.defineInRange("stepsPerDamage", stepsPerDamage, 0, Integer.MAX_VALUE),
				builder.comment("The movement speed bonus horseshoes provide").defineInRange("movementSpeed", movementSpeed, 0, 1024),
				builder.comment("The fall distance bonus horseshoes provide").defineInRange("bonusFallDistance", bonusFallDistance, 0, 64),
				builder.comment("The step height bonus these horseshoes provide").defineInRange("stepHeightBonus", bonusStepDistance, 0, 512));
		builder.pop();
		return config;
	}
}
