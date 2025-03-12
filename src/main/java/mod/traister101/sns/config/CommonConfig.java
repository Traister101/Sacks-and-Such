package mod.traister101.sns.config;

import net.minecraftforge.common.ForgeConfigSpec.*;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class CommonConfig {

	BooleanValue doPickBlock;

	CommonConfig(final Builder builder) {
		doPickBlock = builder.comment("Do pick block for Item Container. Server will trump client config!").define("doPickBlock", true);
	}
}