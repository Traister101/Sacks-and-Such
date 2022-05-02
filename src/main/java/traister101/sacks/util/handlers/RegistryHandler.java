package traister101.sacks.util.handlers;

import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import traister101.sacks.SacksNSuch;
import traister101.sacks.objects.items.ItemsSNS;

@Mod.EventBusSubscriber
public class RegistryHandler {
	
	
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(ItemsSNS.ITEMS.toArray(new Item[0]));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelRegister(ModelRegistryEvent event) {
		for(Item item : ItemsSNS.ITEMS) {
			SacksNSuch.PROXY.registerItemRenderer(item, 0, "inventory");
		}
	}
	
	public static void preInitRegistry() {
		
	}
	
	public static void initRegistry() {
		
	}
	
	public static void postInitRegistry() {
		
	}
	
	public static void serverRegistry() {
		
	}
	
	@SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event) {

        // LEATHER ITEMS

        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(ItemsSNS.UNFINISHED_LEATHER_SACK), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ").setRegistryName("unfinished_leather_sack")
        );
    }
}