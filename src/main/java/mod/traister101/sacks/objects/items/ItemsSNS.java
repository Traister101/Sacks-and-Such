package mod.traister101.sacks.objects.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.api.registries.SackRegistry;
import mod.traister101.sacks.api.types.SackType;
import mod.traister101.sacks.util.VesselType;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.items.ItemMisc;
import net.dries007.tfc.objects.items.ceramics.ItemPottery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.util.SNSUtils.getNull;

@ObjectHolder(MODID)
@EventBusSubscriber(modid = MODID)
public final class ItemsSNS {

	// Sack items
	public static final Item UNFINISHED_LEATHER_SACK = getNull();
	public static final Item REINFORCED_FIBER = getNull();
	public static final Item REINFORCED_FABRIC = getNull();
	// Unfired explosive vessels
	public static final Item UNFIRED_TINY_VESSEL = getNull();
	public static final Item UNFIRED_EXPLOSIVE_VESSEL = getNull();
	// Fired explosive vessels
	public static final Item FIRED_TINY_VESSEL = getNull();
	// Vessels
	public static final Item TINY_EXPLOSIVE_VESSEL = getNull();
	public static final Item EXPLOSIVE_VESSEL = getNull();
	public static final Item STICKY_EXPLOSIVE_VESSEL = getNull();
	private static ImmutableList<ItemSack> allSacks;
	private static ImmutableList<Item> allSimpleItems;
	private static ImmutableList<ItemThrowableVessel> allThrowableVessels;

	public static ImmutableList<Item> getAllSimpleItems() {
		return allSimpleItems;
	}

	public static ImmutableList<ItemSack> getAllSacks() {
		return allSacks;
	}

	public static ImmutableList<ItemThrowableVessel> getAllThrowableVessels() {
		return allThrowableVessels;
	}

	@SubscribeEvent
	public static void registerItems(final Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		final Builder<ItemSack> sacks = ImmutableList.builder();
		final Builder<Item> simpleItems = ImmutableList.builder();
		final Builder<ItemThrowableVessel> throwableVessels = ImmutableList.builder();

		// TODO re-impliment disabling via hiding in JEI and removing recipes
		for (final SackType sackType : SackRegistry.SACK_TYPES.getValuesCollection()) {
			//noinspection DataFlowIssue
			sacks.add(registerSack(registry, sackType.getRegistryName().getPath(), sackType));
		}

		simpleItems.add(register(registry, "unfinished_leather_sack", new ItemMisc(Size.NORMAL, Weight.LIGHT)));
		simpleItems.add(register(registry, "reinforced_fiber", new ItemMisc(Size.NORMAL, Weight.LIGHT)));
		simpleItems.add(register(registry, "reinforced_fabric", new ItemMisc(Size.NORMAL, Weight.LIGHT)));
		simpleItems.add(register(registry, "steel_reinforced_fabric", new ItemMisc(Size.NORMAL, Weight.LIGHT)));

		if (ConfigSNS.EXPLOSIVE_VESSEL.DANGEROUS.isEnabled) {
			throwableVessels.add(registerVessel(registry, "explosive", VesselType.EXPLOSIVE));
			simpleItems.add(registerPottery(registry, "unfired_explosive_vessel"));
			if (ConfigSNS.EXPLOSIVE_VESSEL.DANGEROUS.stickyEnabled)
				throwableVessels.add(registerVessel(registry, "sticky_explosive", VesselType.STICKY));
		}

		if (ConfigSNS.EXPLOSIVE_VESSEL.DANGEROUS.smallEnabled) {
			simpleItems.add(registerPottery(registry, "unfired_tiny_vessel"));
			simpleItems.add(registerPottery(registry, "fired_tiny_vessel"));
			throwableVessels.add(registerVessel(registry, "tiny_explosive", VesselType.TINY));
		}

		allSacks = sacks.build();
		allSimpleItems = simpleItems.build();
		allThrowableVessels = throwableVessels.build();
	}


	private static ItemSack registerSack(final IForgeRegistry<Item> registry, final String name, final SackType type) {
		return register(registry, name, new ItemSack(type), CreativeTabsTFC.CT_MISC);
	}

	private static ItemThrowableVessel registerVessel(final IForgeRegistry<Item> registry, final String name, final VesselType type) {
		return register(registry, name + "_vessel", new ItemThrowableVessel(type), CreativeTabsTFC.CT_MISC);
	}

	private static ItemPottery registerPottery(final IForgeRegistry<Item> registry, final String string) {
		return register(registry, string, new ItemPottery(), CreativeTabsTFC.CT_POTTERY);
	}

	private static <T extends Item> T register(final IForgeRegistry<Item> registry, final String name, final T item) {
		return register(registry, name, item, CreativeTabsTFC.CT_MISC);
	}

	private static <T extends Item> T register(final IForgeRegistry<Item> registry, final String name, final T item, final CreativeTabs ct) {
		item.setRegistryName(MODID, name);
		item.setTranslationKey(MODID + "." + name);
		item.setCreativeTab(ct);
		registry.register(item);
		return item;
	}
}