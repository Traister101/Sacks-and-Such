package mod.traister101.sacks.objects.items;

import static mod.traister101.sacks.SacksNSuch.MODID;

import javax.annotation.Nonnull;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSNS extends Item implements IItemSize {
	
	protected final Weight weight;
	protected final Size size;
	
	public ItemSNS() {
		this.weight = Weight.LIGHT;
		this.size = Size.NORMAL;
	}
	
	public ItemSNS(Weight weight, Size size) {
		this.weight = weight;
		this.size = size;
	}
	
	@Nonnull
	@Override
	public Weight getWeight(@Nonnull ItemStack stack) {
		return weight;
	}
	
	@Nonnull
	@Override
	public Size getSize(@Nonnull ItemStack stack) {
		return size;
	}
}