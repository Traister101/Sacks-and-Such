package mod.traister101.objects.items;

import javax.annotation.Nonnull;

import mod.traister101.Main;
import mod.traister101.item.ModItems;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemBase extends ItemTFC
{
	
	protected String name;
	protected Size size;
	protected Weight weight;
	protected CreativeTabs tab;
	
	
	public ItemBase(String name, CreativeTabs tab) {
		this.name = name;
		//Default item size
		this.size = Size.NORMAL;
		//Default item weight
		this.weight = Weight.LIGHT;
		
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(tab);
		
		ModItems.ITEMS.add(this);
	}
	
	public ItemBase(String name) {
		this.name = name;
		//Default item size
		this.size = Size.NORMAL;
		//Default item weight
		this.weight = Weight.LIGHT;
		
		setTranslationKey(name);
		setRegistryName(name);
		//Default tab
		setCreativeTab(Main.creativeTab);
		
		ModItems.ITEMS.add(this);
	}
	
	//Overrides default size
	public ItemBase setSize(Size size)
	{
		this.size = size;
		return this;
	}
	
	//Overrides default weight
	public ItemBase setWeight(Weight weight)
	{
		this.weight = weight;
		return this;
	}
	
    @Nonnull
    public Size getSize(@Nonnull ItemStack stack) {
        return this.size;
    }

    @Nonnull
    public Weight getWeight(@Nonnull ItemStack stack) {
        return this.weight;
    }

}
