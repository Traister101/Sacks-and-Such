package mod.traister101.objects.items;

import javax.annotation.Nonnull;

import mod.traister101.Main;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemBase extends ItemTFC
{
	
	protected String name;

	public ItemBase(String name) {
		this.name = name;
		setTranslationKey(name);
		setRegistryName(name);
		/*Fiddle with this later
		ModItems.ITEMS.add(this);
		*/
	}
	
	//Registers model
	public void registerItemModel() {
		Main.PROXY.registerItemRenderer(this, 0, name);
	}
	
	//Sets the creative tab to whatever you pass into the method
	@Override
	public ItemBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
	
    @Nonnull
    public Size getSize(@Nonnull ItemStack stack) {
        return Size.NORMAL;
    }

    @Nonnull
    public Weight getWeight(@Nonnull ItemStack stack) {
        return Weight.HEAVY;
    }

}
