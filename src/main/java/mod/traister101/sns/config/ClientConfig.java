package mod.traister101.sns.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public final class ClientConfig {

	public final BooleanValue voidGlint;
	public final BooleanValue displayItemContentsAsImages;

	ClientConfig(final ForgeConfigSpec.Builder builder) {
		voidGlint = builder.comment("Swaps the enchant glint from when auto pickup is enabled to when it's dissabled").define("voidGlint", true);
		displayItemContentsAsImages = builder.comment("When enabled sacks will display their contents like how TFC vessels do")
				.define("displayItemContentsAsImages", true);
	}
}