package mod.traister101.sns.config.entries;

import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class ContainerConfig {

	BooleanValue doPickup;
	BooleanValue doVoiding;
	BooleanValue doInventoryTransfer;
	IntValue slotCount;
	IntValue slotCap;
	EnumValue<Size> allowedSize;

	public static ContainerConfig buildContainerConfig(final Builder builder, final String containerName, final boolean doPickup,
			final boolean doVoiding, final boolean doInventoryTransfer, final int slotCount, final int slotCap, final Size allowedSize) {
		builder.push(containerName);
		final ContainerConfig containerConfig = new ContainerConfig(
				builder.comment("Determines if this container will automatically pickup items").define("doPickup", doPickup),
				builder.comment("Determines if this container can void items on pickup").define("doVoiding", doVoiding),
				builder.comment("Determines if this container can transfer items in an inventory")
						.define("doInventoryTransfer", doInventoryTransfer),
				builder.comment("Controls the amount of slots this container has").defineInRange("slotCount", slotCount, 1, 27),
				builder.comment("Item stack max for the type of container").defineInRange("slotCap", slotCap, 1, 512),
				builder.comment("The maximum item size allowed in the container").defineEnum("allowedSize", allowedSize));
		builder.pop();
		return containerConfig;
	}
}
