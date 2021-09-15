package mod.traister101.util.item;

import java.util.ArrayList;
import java.util.List;

import mod.traister101.objects.items.ItemBase;
import mod.traister101.objects.items.SackBase;
import net.minecraft.item.Item;

public class ModItems {

	//Makes an array that all the items go in
	public static final List<Item> ITEMS = new ArrayList();
	
	//Items
	public static SackBase THATCH_SACK = new SackBase("thatch_sack");
	public static SackBase LEATHER_SACK = new SackBase("leather_sack");
	public static ItemBase UNFINISHED_LEATHER_SACK = new ItemBase("unfinished_leather_sack");
	

}

