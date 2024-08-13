package mod.traister101.sacks.common.items;

import net.dries007.tfc.util.Helpers;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.function.Supplier;

public final class SNSItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SacksNSuch.MODID);

	public static final RegistryObject<Item> UNFINISHED_LEATHER_SACK = registerSimple("unfinished_leather_sack");
	public static final RegistryObject<Item> REINFORCED_FIBER = registerSimple("reinforced_fiber");
	public static final RegistryObject<Item> REINFORCED_FABRIC = registerSimple("reinforced_fabric");
    public static final RegistryObject<Item> PACK_FRAME = registerSimple("pack_frame");

	/**
	 * All our sacks
	 */
	public static final EnumMap<DefaultSacks, RegistryObject<SackItem>> SACKS = Helpers.mapOfKeys(DefaultSacks.class,
			sackType -> register(sackType.getSerializedName(), () -> new SackItem(new Properties().stacksTo(1), sackType)));

	private static RegistryObject<Item> registerSimple(final String name) {
		return register(name, () -> new Item(new Properties()));
	}

	private static <I extends Item> RegistryObject<I> register(final String name, final Supplier<I> itemSupplier) {
		return ITEMS.register(name, itemSupplier);
	}
}