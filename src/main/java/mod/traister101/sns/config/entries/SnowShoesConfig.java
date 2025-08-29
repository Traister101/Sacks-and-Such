package mod.traister101.sns.config.entries;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class SnowShoesConfig {

	DoubleValue speedBonus;
	DoubleValue speedPenalty;

	public static SnowShoesConfig build(final ForgeConfigSpec.Builder builder, final String bootsName, final double speedBonus,
			final double speedPenalty) {
		builder.push(bootsName);
		final var config = new SnowShoesConfig(
				builder.comment("The movement speed bonus when on snow").defineInRange("speedBonus", speedBonus, -1024, 1024),
				builder.comment("The movement speed penalty when not on snow").defineInRange("speedPenalty", speedPenalty, -1042, 1024));
		builder.pop();
		return config;
	}
}