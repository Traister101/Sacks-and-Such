package mod.traister101.sns.config.entries;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class WalkingStickConfig {

	DoubleValue mainHandSpeedBonus;
	DoubleValue offHandSpeedBonus;

	public static WalkingStickConfig build(final ForgeConfigSpec.Builder builder, final String name, final double mainHandSpeedBonus,
			final double offHandSpeedBonus) {
		builder.push(name);
		final var config = new WalkingStickConfig(builder.comment("The movement speed bonus this walking stick provides when in the main hand")
				.defineInRange("mainHandSpeedBonus", mainHandSpeedBonus, 0, 1024),
				builder.comment("The movement speed bonus this walking stick provides when in the off hand")
						.defineInRange("offHandSpeedBonus", offHandSpeedBonus, 0, 1024));
		builder.pop();
		return config;
	}
}