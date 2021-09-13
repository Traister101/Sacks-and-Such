package mod.traister101.item;

import java.util.ArrayList;
import java.util.List;

import mod.traister101.Main;
import mod.traister101.objects.items.ItemBase;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

	//Makes an array that all the items go in
	public static final List<Item> ITEMS = new ArrayList();
	
	public static ItemBase THATCH_SACK = new ItemBase("thatch_sack");
	public static ItemBase LEATHER_SACK = new ItemBase("leather_sack");
	
	
	public static void register(IForgeRegistry<Item> registry) 
	{
		registry.registerAll(
				THATCH_SACK, LEATHER_SACK
		);
	}
	
	public static void registerModels() 
	{
		THATCH_SACK.registerItemModel();
		LEATHER_SACK.registerItemModel();
	}
	
}

