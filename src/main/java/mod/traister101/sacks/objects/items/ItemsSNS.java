package mod.traister101.sacks.objects.items;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.util.SNSUtils.getNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.util.SackType;
import mod.traister101.sacks.util.VesselType;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemPottery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(MODID)
@EventBusSubscriber(modid = MODID)
public final class ItemsSNS {
	
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
	
	// Sack items
	public static final Item UNFINISHED_LEATHER_SACK = getNull();
	
	// Unfired explosive vessels
	public static final Item UNFIRED_TINY_VESSEL = getNull();
	public static final Item UNFIRED_EXPLOSIVE_VESSEL = getNull();
	
	// Fired explosive vessels
	public static final Item FIRED_TINY_VESSEL = getNull();
	
	// Vessels
	public static final Item TINY_EXPLOSIVE_VESSEL = getNull();
	public static final Item EXPLOSIVE_VESSEL = getNull();
	public static final Item STICKY_EXPLOSIVE_VESSEL = getNull();
	
	@SubscribeEvent
	public static void registerItems(Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		Builder<ItemSack> sacks = ImmutableList.builder();
		Builder<Item> simpleItems = ImmutableList.builder();
		Builder<ItemThrowableVessel> throwableVessels = ImmutableList.builder();
		
		if (ConfigSNS.THATCH_SACK.isEnabled) {
			sacks.add(registerSack(registry, "thatch", SackType.THATCH));
		}
		
		if (ConfigSNS.LEATHER_SACK.isEnabled) {
			sacks.add(registerSack(registry, "leather", SackType.LEATHER));
			simpleItems.add(register(registry, "unfinished_leather_sack"));
		}
		
		if (ConfigSNS.BURLAP_SACK.isEnabled) {
			sacks.add(registerSack(registry, "burlap", SackType.BURLAP));
		}
		
		if (ConfigSNS.MINER_SACK.isEnabled) {
			sacks.add(registerSack(registry, "miners", SackType.MINER));
		}
		
//		registerSack(registry, sacks, "food", SackType.FOOD);
		
		if (ConfigSNS.EXPLOSIVE_VESSEL.isEnabled) {
			throwableVessels.add(registerVessel(registry, "explosive", VesselType.EXPLOSIVE));
			simpleItems.add(registerPottery(registry, "unfired_explosive_vessel"));
			if (ConfigSNS.EXPLOSIVE_VESSEL.stickyEnabled) throwableVessels.add(registerVessel(registry, "sticky_explosive", VesselType.STICKY));
		}
		
		if (ConfigSNS.EXPLOSIVE_VESSEL.smallEnabled) {
			simpleItems.add(registerPottery(registry, "unfired_tiny_vessel"));
			simpleItems.add(registerPottery(registry, "fired_tiny_vessel"));
			throwableVessels.add(registerVessel(registry, "tiny_explosive", VesselType.TINY));
		}
		
		allSacks = sacks.build();
		allSimpleItems = simpleItems.build();
		allThrowableVessels = throwableVessels.build();
	}
	

	private static ItemSack registerSack(IForgeRegistry<Item> registry, String name, SackType type) {
		return register(registry, name + "_sack", new ItemSack(type), CreativeTabsTFC.CT_MISC);
	}
	
	private static ItemThrowableVessel registerVessel(IForgeRegistry<Item> registry, String name, VesselType type) {
		return register(registry, name + "_vessel", new ItemThrowableVessel(type), CreativeTabsTFC.CT_MISC);
	}
	
	private static ItemPottery registerPottery(IForgeRegistry<Item> registry, String string) {
		return register(registry, string, new ItemPottery(), CreativeTabsTFC.CT_POTTERY);
	}
	
	private static ItemSNS register(IForgeRegistry<Item> registry, String name) {
		return register(registry, name, new ItemSNS(), CreativeTabsTFC.CT_MISC);
	}
	
	private static <T extends Item> T register(IForgeRegistry<Item> registry, String name, T item) {
		return register(registry, name, item, CreativeTabsTFC.CT_MISC);
	}
	
	private static <T extends Item> T register(IForgeRegistry<Item> registry, String name, T item, CreativeTabs ct) {
		item.setRegistryName(MODID, name);
		item.setTranslationKey(MODID + "." + name);
		item.setCreativeTab(ct);
		registry.register(item);
		return item;
	}
}