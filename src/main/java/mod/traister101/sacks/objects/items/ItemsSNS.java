package mod.traister101.sacks.objects.items;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static mod.traister101.sacks.util.helper.Utils.getNull;
import static net.dries007.tfc.objects.CreativeTabsTFC.CT_MISC;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.util.SackType;
import mod.traister101.sacks.util.VesselType;
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
	private static ImmutableList<ItemThrowableVessel> allThrowableVessels;
	
	public static ImmutableList<ItemSack> getAllSacks() {
		return allSacks;
	}
	
	public static ImmutableList<ItemThrowableVessel> getAllThrowableVessels() {
		return allThrowableVessels;
	}
	
	public static final Item UNFINISHED_LEATHER_SACK = getNull();
	
	@SubscribeEvent
	public static void registerItems(Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		Builder<ItemSack> sacks = ImmutableList.builder();
		Builder<ItemThrowableVessel> throwableVessels = ImmutableList.builder();
		
		registerSack(registry, sacks, "thatch", SackType.THATCH);
		registerSack(registry, sacks, "leather", SackType.LEATHER);
//		registerSack(registry, sacks, "food", SackType.FOOD);
		registerSack(registry, sacks, "burlap", SackType.BURLAP);
		registerSack(registry, sacks, "miners", SackType.MINER);
		
		registerVessel(registry, throwableVessels, "explosive", VesselType.EXPLOSIVE);
		registerVessel(registry, throwableVessels, "sticky", VesselType.STICKY);
		
		if (ConfigSNS.LEATHERSACK.isEnabled) {
			register(registry, "unfinished_leather_sack", new ItemSNS(), CT_MISC);
		}
		
		allSacks = sacks.build();
		allThrowableVessels = throwableVessels.build();
	}
	
	private static void registerSack(IForgeRegistry<Item> registry, Builder<ItemSack> sacks, String name, SackType type) {
		if (SackType.isEnabled(type)) sacks.add(register(registry, name + "_sack", new ItemSack(type), CT_MISC));
	}
	
	private static void registerVessel(IForgeRegistry<Item> registry, Builder<ItemThrowableVessel> throwableVessels, String name, VesselType type) {
		if (VesselType.isEnabled(type)) throwableVessels.add(register(registry, name + "_vessel", new ItemThrowableVessel(type), CT_MISC));
	}
	
	private static <T extends Item> T register(IForgeRegistry<Item> registry, String name, T item, CreativeTabs ct) {
		item.setRegistryName(MODID, name);
		item.setTranslationKey(MODID + "." + name);
		item.setCreativeTab(ct);
		registry.register(item);
		return item;
	}
}