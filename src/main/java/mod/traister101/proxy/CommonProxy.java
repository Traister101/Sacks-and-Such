package mod.traister101.proxy;

import mod.traister101.util.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class CommonProxy
{
	public void registerItemRenderer(Item item, int meta, String id) {
	}
}
