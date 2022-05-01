package mod.traister101.objects.items;

import javax.annotation.Nonnull;

import mod.traister101.Main;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemBase extends ItemTFC {
	
	public ItemBase(String name) {
		
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(Main.creativeTab);
		
		ModItems.ITEMS.add(this);
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
