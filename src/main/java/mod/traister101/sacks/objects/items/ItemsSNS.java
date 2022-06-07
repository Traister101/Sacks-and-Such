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
	
	public static Item UNFINISHED_LEATHER_SACK = getNull();
	
	@SubscribeEvent
	public static void registerItems(Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		Builder<ItemSack> sacks = ImmutableList.builder();
		Builder<ItemThrowableVessel> throwableVessels = ImmutableList.builder();
		sacks.add(register(registry, "thatch_sack", new ItemSack(SackType.THATCH), CT_MISC));
//		sacks.add(register(registry, "food_sack", new ItemSack(SackType.FOOD), CT_MISC));
		if (ConfigSNS.General.LEATHERSACK.enabled) {
			sacks.add(register(registry, "leather_sack", new ItemSack(SackType.LEATHER), CT_MISC));
			register(registry, "unfinished_leather_sack", new ItemSNS(), CT_MISC);
		}
		sacks.add(register(registry, "burlap_sack", new ItemSack(SackType.BURLAP), CT_MISC));
		sacks.add(register(registry, "miners_sack", new ItemSack(SackType.MINER), CT_MISC));
		
		throwableVessels.add(register(registry, "explosive_vessel", new ItemThrowableVessel(VesselType.EXPLOSIVE), CT_MISC));
		throwableVessels.add(register(registry, "sticky_vessel", new ItemThrowableVessel(VesselType.STICKY), CT_MISC));
		
		allSacks = sacks.build();
		allThrowableVessels = throwableVessels.build();
	}
	
	private static <T extends Item> T register(IForgeRegistry<Item> registry, String name, T item, CreativeTabs ct) {
		item.setRegistryName(MODID, name);
		item.setTranslationKey(MODID + "." + name);
		item.setCreativeTab(ct);
		registry.register(item);
		return item;
	}
}