package mod.traister101.sns.config;

import mod.traister101.sns.config.entries.*;
import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraftforge.common.ForgeConfigSpec.*;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class ServerConfig {

	// Containers
	ContainerConfig strawBasket;
	ContainerConfig leatherSack;
	ContainerConfig burlapSack;
	ContainerConfig oreSack;
	ContainerConfig seedPouch;
	ContainerConfig framePack;
	ContainerConfig lunchBox;
	ContainerConfig quiver;

	// Boots
	IntValue bootsStepPerDamage;
	BootsConfig hikingBoots;
	BootsConfig steelToeHikingBoots;
	BootsConfig blackSteelToeHikingBoots;
	BootsConfig blueSteelToeHikingBoots;
	BootsConfig redSteelToeHikingBoots;

	// Horseshoes
	IntValue horseshoesStepsPerDamage;
	HorseshoesConfig steelHorseshoes;
	HorseshoesConfig blackSteelHorseshoes;
	HorseshoesConfig blueSteelHorseshoes;
	HorseshoesConfig redSteelHorseshoes;

	// Globals
	BooleanValue doPickup;
	BooleanValue doVoiding;
	BooleanValue allPickBlock;
	BooleanValue enableContainerInventoryInteraction;

	// Misc
	DoubleValue traitLunchboxModifier;
	DoubleValue maximumNetCaptureSize;

	ServerConfig(final Builder builder) {

		builder.push("Container Item Config");

		strawBasket = ContainerConfig.build(builder, "Straw Basket", true, true, true, 4, 32, Size.SMALL);
		leatherSack = ContainerConfig.build(builder, "Leather Sack", false, false, true, 4, 64, Size.NORMAL);
		burlapSack = ContainerConfig.build(builder, "Burlap Sack", true, true, false, 8, 48, Size.SMALL);
		oreSack = ContainerConfig.build(builder, "Ore Sack", true, false, true, 1, 512, Size.SMALL);
		seedPouch = ContainerConfig.build(builder, "Seed Pouch", true, false, true, 27, 64, Size.SMALL);
		framePack = ContainerConfig.build(builder, "Frame Pack", false, false, false, 18, 64, Size.LARGE);
		lunchBox = ContainerConfig.build(builder, "Lunch Box", false, false, true, 8, 4, Size.NORMAL);
		quiver = ContainerConfig.build(builder, "Quiver", true, false, false, 8, 32, Size.VERY_LARGE);

		builder.pop();

		builder.push("Boot config");
		bootsStepPerDamage = builder.comment("The amount of steps taken before one point of durability is lost")
				.defineInRange("bootsStepPerDamage", 500, 0, Integer.MAX_VALUE);

		hikingBoots = BootsConfig.build(builder, "Hiking Boots", 0.05, 0, 0.5);
		steelToeHikingBoots = BootsConfig.build(builder, "Steel Toe Boots", 0.1, 0.5, 1);
		blackSteelToeHikingBoots = BootsConfig.build(builder, "Black Steel Toe Boots", 0.15, 0.5, 2);
		blueSteelToeHikingBoots = BootsConfig.build(builder, "Blue Steel Toe Boots", 0.2, 0.5, 5);
		redSteelToeHikingBoots = BootsConfig.build(builder, "Red Steel Toe Boots", 0.2, 0.5, 5);

		builder.pop();

		builder.push("Horseshoes config");

		horseshoesStepsPerDamage = builder.comment("The amount of steps taken before one point of durability is lost")
				.defineInRange("horseshoesStepsPerDamage", 500, 0, Integer.MAX_VALUE);
		steelHorseshoes = HorseshoesConfig.build(builder, "Steel Horseshoes", 0.05, 2, 0);
		blackSteelHorseshoes = HorseshoesConfig.build(builder, "Black Steel Horseshoes", 0.1, 2, 0);
		blueSteelHorseshoes = HorseshoesConfig.build(builder, "Blue Steel Horseshoes", 0.2, 5, 1);
		redSteelHorseshoes = HorseshoesConfig.build(builder, "Red Steel Horseshoes", 0.2, 5, 1);

		builder.pop();

		builder.push("Global config");
		doPickup = builder.comment("Global control for automatic pickup, this will not force enable for every type")
				.worldRestart()
				.define("doPickup", true);
		doVoiding = builder.comment("A global toggle for item voiding, this will not force enable for every type").define("doVoiding", true);
		allPickBlock = builder.comment("This allows other containers such as vessels to support the pick block search").define("allPickBlock", false);
		enableContainerInventoryInteraction = builder.comment(
						"This allows containers to have items inserted and extracted from them via the inventory like vanilla Bundles")
				.define("enableContainerInventoryInteraction", true);

		traitLunchboxModifier = builder.comment(
						"The modifier for the 'Lunchbox' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.")
				.defineInRange("traitLunchboxModifier", 0.6, 0.0, Double.MAX_VALUE);
		maximumNetCaptureSize = builder.comment(
						"The maximum size of an entity which mob nets can capture. This is not directly related to TFC's animal size stat.")
				.defineInRange("maximumNetCaptureSize", 0.5, 0, Double.MAX_VALUE);
	}
}