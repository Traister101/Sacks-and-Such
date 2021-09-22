package mod.traister101.objects.items;

import java.util.ArrayList;
import java.util.List;

import mod.traister101.util.handlers.GuiHandler;
import net.minecraft.item.Item;

public class ModItems {

	//Makes an array that all the items go in
	public static final List<Item> ITEMS = new ArrayList();
	
	//Items
	public static Item THATCH_SACK = new ItemSack("thatch_sack", GuiHandler.Type.SACK_THATCH);
	public static Item LEATHER_SACK = new ItemSack("leather_sack", GuiHandler.Type.SACK_LEATHER);
	public static Item UNFINISHED_LEATHER_SACK = new ItemBase("unfinished_leather_sack");
	

}

