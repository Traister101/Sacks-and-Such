package traister101.sacks.proxy;

import static traister101.sacks.SacksNSuch.MODID;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonProxy
{
	public void registerItemRenderer(Item item, int meta, String id) {
	}
}
