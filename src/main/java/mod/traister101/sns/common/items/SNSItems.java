package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.BootsArmorMaterial;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.util.Metal.Default;
import net.dries007.tfc.util.registry.RegistryMetal;

import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;

import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class SNSItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SacksNSuch.MODID);

	// Crafting items
	public static final RegistryObject<Item> UNFINISHED_LEATHER_SACK = registerSimple("unfinished_leather_sack");
	public static final RegistryObject<Item> REINFORCED_FIBER = registerSimple("reinforced_fiber");
	public static final RegistryObject<Item> REINFORCED_FABRIC = registerSimple("reinforced_fabric");
	public static final RegistryObject<Item> PACK_FRAME = registerSimple("pack_frame", new Properties().rarity(Rarity.UNCOMMON));
	public static final RegistryObject<Item> LEATHER_STRIP = registerSimple("leather_strip");
	public static final RegistryObject<Item> BOUND_LEATHER_STRIP = registerSimple("bound_leather_strip");
	public static final RegistryObject<Item> BUCKLE = registerSimple("buckle");
	public static final RegistryObject<Item> STEEL_HORSESHOE = registerHorseshoe(Default.STEEL);
	public static final RegistryObject<Item> BLACK_STEEL_HORSESHOE = registerHorseshoe(Default.BLACK_STEEL);
	public static final RegistryObject<Item> BLUE_STEEL_HORSESHOE = registerHorseshoe(Default.BLUE_STEEL);
	public static final RegistryObject<Item> RED_STEEL_HORSESHOE = registerHorseshoe(Default.RED_STEEL);

	// Container Items
	public static final RegistryObject<ContainerItem> STRAW_BASKET = registerContainerItem(DefaultContainers.STRAW_BASKET);
	public static final RegistryObject<ContainerItem> LEATHER_SACK = registerContainerItem(DefaultContainers.LEATHER_SACK);
	public static final RegistryObject<ContainerItem> BURLAP_SACK = registerContainerItem(DefaultContainers.BURLAP_SACK);
	public static final RegistryObject<ContainerItem> ORE_SACK = registerContainerItem(DefaultContainers.ORE_SACK);
	public static final RegistryObject<ContainerItem> SEED_POUCH = registerContainerItem(DefaultContainers.SEED_POUCH);
	public static final RegistryObject<ContainerItem> FRAME_PACK = registerContainerItem(DefaultContainers.FRAME_PACK,
			new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	public static final RegistryObject<LunchBoxItem> LUNCHBOX = register("lunchbox",
			() -> new LunchBoxItem(new Properties(), DefaultContainers.LUNCHBOX));

	public static final RegistryObject<MobNetItem> MOB_NET_ITEM = register("mob_net", () -> new MobNetItem(new Properties()));

	public static final RegistryObject<HikingBootsItem> HIKING_BOOTS = register("hiking_boots",
			() -> new HikingBootsItem(new Properties().stacksTo(1), BootsArmorMaterial.HIKING_BOOTS, SNSConfig.SERVER.hikingBoots.movementSpeed,
					SNSConfig.SERVER.hikingBoots.stepHeight));

	public static final RegistryObject<HikingBootsItem> STEEL_TOE_HIKING_BOOTS = register("steel_toe_hiking_boots",
			() -> new HikingBootsItem(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON), BootsArmorMaterial.STEEL_TOE_HIKING_BOOTS,
					SNSConfig.SERVER.steelToeHikingBoots.movementSpeed, SNSConfig.SERVER.steelToeHikingBoots.stepHeight));

	public static final RegistryObject<HikingBootsItem> BLACK_STEEL_TOE_HIKING_BOOTS = register("black_steel_toe_hiking_boots",
			() -> new HikingBootsItem(new Properties().stacksTo(1).rarity(Rarity.RARE), BootsArmorMaterial.BLACK_STEEL_TOE_HIKING_BOOTS,
					SNSConfig.SERVER.blackSteelToeHikingBoots.movementSpeed, SNSConfig.SERVER.blackSteelToeHikingBoots.stepHeight));

	public static final RegistryObject<HikingBootsItem> BLUE_STEEL_TOE_HIKING_BOOTS = register("blue_steel_toe_hiking_boots",
			() -> new HikingBootsItem(new Properties().stacksTo(1).rarity(Rarity.EPIC), BootsArmorMaterial.BLUE_STEEL_TOE_HIKING_BOOTS,
					SNSConfig.SERVER.blueSteelToeHikingBoots.movementSpeed, SNSConfig.SERVER.blueSteelToeHikingBoots.stepHeight));

	public static final RegistryObject<HikingBootsItem> RED_STEEL_TOE_HIKING_BOOTS = register("red_steel_toe_hiking_boots",
			() -> new HikingBootsItem(new Properties().stacksTo(1).rarity(Rarity.EPIC), BootsArmorMaterial.RED_STEEL_TOE_HIKING_BOOTS,
					SNSConfig.SERVER.redSteelToeHikingBoots.movementSpeed, SNSConfig.SERVER.redSteelToeHikingBoots.stepHeight));

	public static final RegistryObject<HorseshoesItem> STEEL_HORSESHOES = registerHorseShoes(Default.STEEL,
			SNSConfig.SERVER.steelHorseshoeSpeedModifier);

	public static final RegistryObject<HorseshoesItem> BLACK_STEEL_HORSESHOES = registerHorseShoes(Default.BLACK_STEEL,
			SNSConfig.SERVER.blackSteelHorseshoeSpeedModifier);

	public static final RegistryObject<HorseshoesItem> BLUE_STEEL_HORSESHOES = registerHorseShoes(Default.BLUE_STEEL,
			SNSConfig.SERVER.blueSteelHorseshoeSpeedModifier);

	public static final RegistryObject<HorseshoesItem> RED_STEEL_HORSESHOES = registerHorseShoes(Default.RED_STEEL,
			SNSConfig.SERVER.blueSteelHorseshoeSpeedModifier);

	private static RegistryObject<Item> registerHorseshoe(final RegistryMetal metal) {
		return registerSimple("metal/horseshoe/" + metal.getSerializedName(), new Properties().rarity(metal.getRarity()));
	}

	private static RegistryObject<HorseshoesItem> registerHorseShoes(final RegistryMetal metal, final Supplier<Double> horseshoeSpeedModifier) {
		return register("metal/horseshoes/" + metal.getSerializedName(),
				() -> new HorseshoesItem(new Properties().durability(metal.toolTier().getUses()).rarity(metal.getRarity()), horseshoeSpeedModifier));
	}

	private static RegistryObject<ContainerItem> registerContainerItem(final ContainerType containerType) {
		return registerContainerItem(containerType, new Properties().stacksTo(1));
	}

	private static RegistryObject<ContainerItem> registerContainerItem(final ContainerType containerType, final Properties properties) {
		return register(containerType.getSerializedName(), () -> new ContainerItem(properties, containerType));
	}

	private static RegistryObject<Item> registerSimple(final String name) {
		return registerSimple(name, new Properties());
	}

	private static RegistryObject<Item> registerSimple(final String name, final Properties properties) {
		return register(name, () -> new Item(properties));
	}

	private static <I extends Item> RegistryObject<I> register(final String name, final Supplier<I> itemSupplier) {
		return ITEMS.register(name, itemSupplier);
	}
}