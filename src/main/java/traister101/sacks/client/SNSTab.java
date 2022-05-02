package traister101.sacks.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import traister101.sacks.objects.items.ItemsSNS;


import static traister101.sacks.SacksNSuch.MODID;

public class SNSTab extends CreativeTabs {

	public SNSTab() {
		super(MODID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ItemsSNS.THATCH_SACK);
	}
}
