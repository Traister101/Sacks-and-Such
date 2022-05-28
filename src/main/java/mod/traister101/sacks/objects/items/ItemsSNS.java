package mod.traister101.sacks.objects.items;

import java.util.ArrayList;
import java.util.List;

import mod.traister101.sacks.util.SackType;
import mod.traister101.sacks.util.VesselType;
import net.minecraft.item.Item;

public class ItemsSNS {
	// Makes a list that all the items go in for simple registry
	public static final List<Item> ITEMS = new ArrayList();
	
	// Items
	public static Item THATCH_SACK = new ItemSack("thatch_sack", SackType.THATCH);
	public static Item LEATHER_SACK = new ItemSack("leather_sack", SackType.LEATHER);
	public static Item BURLAP_SACK = new ItemSack("burlap_sack", SackType.BURLAP);
	public static Item MINERS_SACK = new ItemSack("miners_sack", SackType.MINER);
	
//	public static Item FOOD_SACK = new FoodSack();
	public static Item EXPLOSIVE_VESSEL = new ItemThrowableVessel("explosive_vessel", VesselType.EXPLOSIVE);
	
	public static Item UNFINISHED_LEATHER_SACK = new ItemSNS("unfinished_leather_sack");

}