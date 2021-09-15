package mod.traister101.client;

import mod.traister101.util.Reference;
import mod.traister101.util.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModTab extends CreativeTabs {

	public ModTab() {
		super(Reference.MODID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ModItems.THATCH_SACK);
	}
}
