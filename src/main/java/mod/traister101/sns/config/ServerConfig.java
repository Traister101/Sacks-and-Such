package mod.traister101.sns.config;

import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

import lombok.*;

public final class ServerConfig {

	// Containers
	public final ContainerConfig strawBasket;
	public final ContainerConfig leatherSack;
	public final ContainerConfig burlapSack;
	public final ContainerConfig oreSack;
	public final ContainerConfig seedPouch;
	public final ContainerConfig framePack;
	public final ContainerConfig lunchBox;
	public final ContainerConfig quiver;

	// Boots
	public final IntValue bootsStepPerDamage;
	public final BootsConfig hikingBoots;
	public final BootsConfig steelToeHikingBoots;
	public final BootsConfig blackSteelToeHikingBoots;
	public final BootsConfig blueSteelToeHikingBoots;
	public final BootsConfig redSteelToeHikingBoots;

	// Horseshoes
	public final IntValue horseshoesStepsPerDamage;
	public final HorseshoesConfig steelHorseshoes;
	public final HorseshoesConfig blackSteelHorseshoes;
	public final HorseshoesConfig blueSteelHorseshoes;
	public final HorseshoesConfig redSteelHorseshoes;

	// Globals
	public final BooleanValue doPickup;
	public final BooleanValue doVoiding;
	public final BooleanValue allPickup;
	public final BooleanValue allPickBlock;
	public final BooleanValue allAllowOre;
	public final BooleanValue allAllowFood;
	public final BooleanValue enableContainerInventoryInteraction;

	// Misc
	public final DoubleValue traitLunchboxModifier;
	public final DoubleValue maximumNetCaptureSize;

	ServerConfig(final ForgeConfigSpec.Builder builder) {

		builder.push("Container Item Config");

		strawBasket = ContainerConfig.buildContainerConfig(builder, "Straw Basket", true, true, true, 4, 32, Size.SMALL);
		leatherSack = ContainerConfig.buildContainerConfig(builder, "Leather Sack", false, false, true, 4, 64, Size.NORMAL);
		burlapSack = ContainerConfig.buildContainerConfig(builder, "Burlap Sack", true, true, false, 8, 48, Size.SMALL);
		oreSack = ContainerConfig.buildContainerConfig(builder, "Ore Sack", true, false, true, 1, 512, Size.SMALL);
		seedPouch = ContainerConfig.buildContainerConfig(builder, "Seed Pouch", true, false, true, 27, 64, Size.SMALL);
		framePack = ContainerConfig.buildContainerConfig(builder, "Frame Pack", false, false, false, 18, 64, Size.LARGE);
		lunchBox = ContainerConfig.buildContainerConfig(builder, "Lunch Box", false, false, true, 8, 4, Size.NORMAL);
		quiver = ContainerConfig.buildContainerConfig(builder, "Quiver", true, false, false, 8, 32, Size.VERY_LARGE);

		builder.pop();

		builder.push("Boot config");
		bootsStepPerDamage = builder.comment("The amount of steps taken before one point of durability is lost")
				.defineInRange("bootsStepPerDamage", 500, 0, Integer.MAX_VALUE);

		hikingBoots = BootsConfig.buildBootsConfig(builder, "Hiking Boots", 0.05, 0, 0.5);
		steelToeHikingBoots = BootsConfig.buildBootsConfig(builder, "Steel Toe Boots", 0.1, 0.5, 1);
		blackSteelToeHikingBoots = BootsConfig.buildBootsConfig(builder, "Black Steel Toe Boots", 0.15, 0.5, 2);
		blueSteelToeHikingBoots = BootsConfig.buildBootsConfig(builder, "Blue Steel Toe Boots", 0.2, 0.5, 5);
		redSteelToeHikingBoots = BootsConfig.buildBootsConfig(builder, "Red Steel Toe Boots", 0.2, 0.5, 5);

		builder.pop();

		builder.push("Horseshoes config");

		horseshoesStepsPerDamage = builder.comment("The amount of steps taken before one point of durability is lost")
				.defineInRange("horseshoesStepsPerDamage", 500, 0, Integer.MAX_VALUE);
		steelHorseshoes = HorseshoesConfig.buildConfig(builder, "Steel Horseshoes", 0.05, 2, 0);
		blackSteelHorseshoes = HorseshoesConfig.buildConfig(builder, "Black Steel Horseshoes", 0.1, 2, 0);
		blueSteelHorseshoes = HorseshoesConfig.buildConfig(builder, "Blue Steel Horseshoes", 0.2, 5, 1);
		redSteelHorseshoes = HorseshoesConfig.buildConfig(builder, "Red Steel Horseshoes", 0.2, 5, 1);

		builder.pop();

		builder.push("Global config");
		doPickup = builder.comment("Global control for automatic pickup, this will not force enable for every type")
				.worldRestart()
				.define("doPickup", true);
		doVoiding = builder.comment("A global toggle for item voiding, this will not force enable for every type").define("doVoiding", true);
		allPickup = builder.comment("Enable auto pickup for other container like items such as the TFC vessel.",
				"This may not always work as expected enable at your own discretion").define("allPickup", false);
		allPickBlock = builder.comment("This allows other containers such as vessels to support the pick block search").define("allPickBlock", false);
		allAllowOre = builder.comment("This makes all container types capable of holding ore").define("allAllowOre", false);
		allAllowFood = builder.comment("This makes all container types capable of holding food although they won't preserve it!")
				.define("allAllowFood", false);
		enableContainerInventoryInteraction = builder.comment(
						"This allows containers to have items inserted and extracted from them via the inventory like vanilla Bundles")
				.define("enableContainerInventoryInteraction", true);

		traitLunchboxModifier = builder.comment(
						"The modifer for the 'Lunchbox' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.")
				.defineInRange("traitLunchboxModifier", 0.6, 0.0, Double.MAX_VALUE);
		maximumNetCaptureSize = builder.comment(
						"The maximum size of an entity which mob nets can capture. This is not directly related to TFC's animal size stat.")
				.defineInRange("maximumNetCaptureSize", 0.5, 0, Double.MAX_VALUE);
	}

	public static final class ContainerConfig {

		public final BooleanValue doPickup;
		public final BooleanValue doVoiding;
		public final BooleanValue doInventoryTransfer;
		public final IntValue slotCount;
		public final IntValue slotCap;
		public final EnumValue<Size> allowedSize;

		private ContainerConfig(final ForgeConfigSpec.Builder builder, final boolean doPickup, final boolean doVoiding,
				final boolean doInventoryTransfer, final int slotCount, final int slotCap, final Size allowedSize) {
			this.doPickup = builder.comment("Determines if this container will automatically pickup items").define("doPickup", doPickup);
			this.doVoiding = builder.comment("Determines if this container can void items on pickup").define("doVoiding", doVoiding);
			this.doInventoryTransfer = builder.comment("Determines if this container can transfer items in an inventory")
					.define("doInventoryTransfer", doInventoryTransfer);
			this.slotCount = builder.comment("This config has a realistic cap of 27 as any higher the slots are added on top of the player slots")
					.defineInRange("slotCount", slotCount, 1, 27);
			this.slotCap = builder.comment("Item stack max for the type of container").defineInRange("slotCap", slotCap, 1, 512);
			this.allowedSize = builder.comment("The maximum item size allowed in the container").defineEnum("allowedSize", allowedSize);
		}

		private static ContainerConfig buildContainerConfig(final Builder builder, final String containerName, final boolean doPickup,
				final boolean doVoiding, final boolean doInventoryTransfer, final int slotCount, final int slotCap, final Size allowedSize) {
			builder.push(containerName);
			final ContainerConfig containerConfig = new ContainerConfig(builder, doPickup, doVoiding, doInventoryTransfer, slotCount, slotCap,
					allowedSize);
			builder.pop();
			return containerConfig;
		}
	}

	public static final class BootsConfig {

		public final DoubleValue movementSpeed;
		public final DoubleValue stepHeight;
		public final DoubleValue fallPadding;

		private BootsConfig(final ForgeConfigSpec.Builder builder, final double movementSpeed, final double stepHeight, final double fallPadding) {
			this.movementSpeed = builder.comment("The movement speed bonus these boots provide")
					.defineInRange("movementSpeed", movementSpeed, 0, 1024);
			this.stepHeight = builder.comment("The step height bonus these boots provide").defineInRange("stepHeight", stepHeight, 0, 512);
			this.fallPadding = builder.comment("The extra fall distance in blocks before you begin taking fall damage")
					.defineInRange("fallPadding", fallPadding, 0, 64);
		}

		public static BootsConfig buildBootsConfig(final Builder builder, final String bootsName, final double movementSpeed, final double stepHeight,
				final double fallPadding) {
			builder.push(bootsName);
			final BootsConfig bootsConfig = new BootsConfig(builder, movementSpeed, stepHeight, fallPadding);
			builder.pop();
			return bootsConfig;
		}
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class HorseshoesConfig {

		public final DoubleValue movementSpeed;
		public final DoubleValue bonusFallDistance;
		public final DoubleValue bonusStepDistance;

		public static HorseshoesConfig buildConfig(final Builder builder, final String bootsName, final double movementSpeed,
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
}