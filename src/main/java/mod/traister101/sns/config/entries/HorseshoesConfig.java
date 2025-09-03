package mod.traister101.sns.config.entries;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class HorseshoesConfig {

	DoubleValue movementSpeed;
	DoubleValue bonusFallDistance;
	DoubleValue bonusStepDistance;

	public static HorseshoesConfig build(final ForgeConfigSpec.Builder builder, final String bootsName, final double movementSpeed,
			final double bonusFallDistance, final double bonusStepDistance) {
		builder.push(bootsName);
		final HorseshoesConfig bootsConfig = new HorseshoesConfig(
				builder.comment("The movement speed bonus horseshoes provide").defineInRange("movementSpeed", movementSpeed, 0, 1024),
				builder.comment("The fall distance bonus horseshoes provide").defineInRange("bonusFallDistance", bonusFallDistance, 0, 64),
				builder.comment("The step height bonus these horseshoes provide").defineInRange("stepHeightBonus", bonusStepDistance, 0, 512));
		builder.pop();
		return bootsConfig;
	}
}
