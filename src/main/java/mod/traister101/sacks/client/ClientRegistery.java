package mod.traister101.sacks.client;

import static mod.traister101.sacks.SacksNSuch.MODID;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import mod.traister101.sacks.objects.items.ItemsSNS;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT, modid = MODID)
public final class ClientRegistery {
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		// ITEMS //
		for (ItemSack sack : ItemsSNS.getAllSacks()) registerBasicItemRenderer(sack);
		
		for (ItemThrowableVessel vessel : ItemsSNS.getAllThrowableVessels()) registerBasicItemRenderer(vessel);
		
		if (ConfigSNS.LEATHERSACK.isEnabled) registerBasicItemRenderer(ItemsSNS.UNFINISHED_LEATHER_SACK);
		
		if (ConfigSNS.EXPLOSIVE_VESSEL.smallEnabled) {
			registerBasicItemRenderer(ItemsSNS.UNFIRED_TINY_EXPLOSIVE_VESSEL);
			registerBasicItemRenderer(ItemsSNS.TINY_EXPLOSIVE_VESSEL);
		}
		
		if (ConfigSNS.EXPLOSIVE_VESSEL.isEnabled) {
			registerBasicItemRenderer(ItemsSNS.UNFIRED_EXPLOSIVE_VESSEL);
		}
	}
	
	private static void registerBasicItemRenderer(Item item) {
		registerItemRenderer(item, 0, "inventory");
	}
	
	private static void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}
}