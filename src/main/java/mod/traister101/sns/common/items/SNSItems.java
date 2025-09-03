package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.SNSArmorMaterials;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.config.entries.HorseshoesConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.util.Metal.Default;
import net.dries007.tfc.util.registry.RegistryMetal;

import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;

import net.minecraftforge.registries.*;

import java.util.function.Function;

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
	public static final RegistryObject<ContainerItem> STRAW_BASKET = registerContainerItem("straw_basket", DefaultContainers.STRAW_BASKET);
	public static final RegistryObject<ContainerItem> LEATHER_SACK = registerContainerItem("leather_sack", DefaultContainers.LEATHER_SACK);
	public static final RegistryObject<ContainerItem> BURLAP_SACK = registerContainerItem("burlap_sack", DefaultContainers.BURLAP_SACK);
	public static final RegistryObject<ContainerItem> ORE_SACK = registerContainerItem("ore_sack", DefaultContainers.ORE_SACK);
	public static final RegistryObject<ContainerItem> SEED_POUCH = registerContainerItem("seed_pouch", DefaultContainers.SEED_POUCH);
	public static final RegistryObject<ContainerItem> FRAME_PACK = registerContainerItem("frame_pack", DefaultContainers.FRAME_PACK,
			new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	public static final RegistryObject<LunchBoxItem> LUNCHBOX = register("lunchbox",
			properties -> new LunchBoxItem(properties, DefaultContainers.LUNCHBOX));
	public static final RegistryObject<ContainerItem> QUIVER = registerContainerItem("quiver", DefaultContainers.QUIVER);

	public static final RegistryObject<MobNetItem> MOB_NET_ITEM = register("mob_net", MobNetItem::new);

	public static final RegistryObject<HikingBootsItem> HIKING_BOOTS = register("hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.HIKING_BOOTS, SNSConfig.SERVER.hikingBoots),
			new Properties().stacksTo(1));

	public static final RegistryObject<HikingBootsItem> STEEL_TOE_HIKING_BOOTS = register("steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.STEEL_TOE_HIKING_BOOTS, SNSConfig.SERVER.steelToeHikingBoots),
			new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

	public static final RegistryObject<HikingBootsItem> BLACK_STEEL_TOE_HIKING_BOOTS = register("black_steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.BLACK_STEEL_TOE_HIKING_BOOTS, SNSConfig.SERVER.blackSteelToeHikingBoots),
			new Properties().stacksTo(1).rarity(Rarity.RARE));

	public static final RegistryObject<HikingBootsItem> BLUE_STEEL_TOE_HIKING_BOOTS = register("blue_steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.BLUE_STEEL_TOE_HIKING_BOOTS, SNSConfig.SERVER.blueSteelToeHikingBoots),
			new Properties().stacksTo(1).rarity(Rarity.EPIC));

	public static final RegistryObject<HikingBootsItem> RED_STEEL_TOE_HIKING_BOOTS = register("red_steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.RED_STEEL_TOE_HIKING_BOOTS, SNSConfig.SERVER.redSteelToeHikingBoots),
			new Properties().stacksTo(1).rarity(Rarity.EPIC));

	public static final RegistryObject<HorseshoesItem> STEEL_HORSESHOES = registerHorseShoes(Default.STEEL, SNSConfig.SERVER.steelHorseshoes);

	public static final RegistryObject<HorseshoesItem> BLACK_STEEL_HORSESHOES = registerHorseShoes(Default.BLACK_STEEL,
			SNSConfig.SERVER.blackSteelHorseshoes);

	public static final RegistryObject<HorseshoesItem> BLUE_STEEL_HORSESHOES = registerHorseShoes(Default.BLUE_STEEL,
			SNSConfig.SERVER.blueSteelHorseshoes);

	public static final RegistryObject<HorseshoesItem> RED_STEEL_HORSESHOES = registerHorseShoes(Default.RED_STEEL,
			SNSConfig.SERVER.redSteelHorseshoes);

	private static RegistryObject<Item> registerHorseshoe(final RegistryMetal metal) {
		return registerSimple("metal/horseshoe/" + metal.getSerializedName(), new Properties().rarity(metal.getRarity()));
	}

	private static RegistryObject<HorseshoesItem> registerHorseShoes(final RegistryMetal metal, final HorseshoesConfig horseshoesConfig) {
		return register("metal/horseshoes/" + metal.getSerializedName(), properties -> new HorseshoesItem(properties, horseshoesConfig),
				new Properties().durability(metal.toolTier().getUses()).rarity(metal.getRarity()));
	}

	private static RegistryObject<ContainerItem> registerContainerItem(final String name, final ContainerType containerType) {
		return registerContainerItem(name, containerType, new Properties().stacksTo(1));
	}

	private static RegistryObject<ContainerItem> registerContainerItem(final String name, final ContainerType containerType,
			final Properties properties) {
		return register(name, prop -> new ContainerItem(prop, containerType), properties);
	}

	private static RegistryObject<Item> registerSimple(final String name) {
		return register(name, Item::new);
	}

	private static RegistryObject<Item> registerSimple(final String name, final Properties properties) {
		return register(name, Item::new, properties);
	}

	private static <I extends Item> RegistryObject<I> register(final String name, final Function<Properties, ? extends I> itemFactory) {
		return register(name, itemFactory, new Properties());
	}

	private static <I extends Item> RegistryObject<I> register(final String name, final Function<Properties, ? extends I> itemFactory,
			final Properties properties) {
		return ITEMS.register(name, () -> itemFactory.apply(properties));
	}
}