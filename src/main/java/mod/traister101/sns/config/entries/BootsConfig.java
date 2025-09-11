package mod.traister101.sns.config.entries;

import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class BootsConfig {

	IntValue stepsPerDamage;
	DoubleValue movementSpeed;
	DoubleValue stepHeight;
	DoubleValue fallPadding;

	public static BootsConfig build(final Builder builder, final String bootsName, final int stepsPerDamage, final double movementSpeed,
			final double stepHeight, final double fallPadding) {
		builder.push(bootsName);
		final BootsConfig bootsConfig = new BootsConfig(builder.comment("The amount of 'steps' taken before one point of durability is lost",
						"Steps are defined as being any change in position while grounded between ticks (IE over 1 second 20 'steps' occur)")
				.defineInRange("stepsPerDamage", stepsPerDamage, 0, Integer.MAX_VALUE),
				builder.comment("The movement speed bonus these boots provide").defineInRange("movementSpeed", movementSpeed, 0, 1024),
				builder.comment("The step height bonus these boots provide").defineInRange("stepHeight", stepHeight, 0, 512),
				builder.comment("The extra fall distance in blocks before you begin taking fall damage")
						.defineInRange("fallPadding", fallPadding, 0, 64));
		builder.pop();
		return bootsConfig;
	}
}
