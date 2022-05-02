package traister101.sacks.objects.items;

import javax.annotation.Nonnull;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import traister101.sacks.SacksNSuch;

import static traister101.sacks.SacksNSuch.MODID;

public class ItemBase extends Item implements IItemSize {

	public ItemBase(String name) {

		setTranslationKey(MODID + "." + name);
		setRegistryName(name);
		setCreativeTab(SacksNSuch.creativeTab);

		ItemsSNS.ITEMS.add(this);
	}

	@Nonnull
	public Size getSize(@Nonnull ItemStack stack) {
		return Size.NORMAL;
	}

	@Nonnull
	public Weight getWeight(@Nonnull ItemStack stack) {
		return Weight.LIGHT;
	}
}