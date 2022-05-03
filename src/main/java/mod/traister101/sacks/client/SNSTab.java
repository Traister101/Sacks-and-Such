package mod.traister101.sacks.client;

import static mod.traister101.sacks.SacksNSuch.MODID;

import mod.traister101.sacks.objects.items.ItemsSNS;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class SNSTab extends CreativeTabs {

	public SNSTab() {
		super(MODID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ItemsSNS.THATCH_SACK);
	}
}
