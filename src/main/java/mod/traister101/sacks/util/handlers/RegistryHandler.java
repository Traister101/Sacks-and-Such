package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.objects.items.ItemsSNS;
import mod.traister101.sacks.util.helper.IConfigurable;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

// TODO figure out good way to have sack item parts go away too

@Mod.EventBusSubscriber
public class RegistryHandler {
	
	
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		if (ConfigSNS.Global.enabled) {
			for (Item item : ItemsSNS.ITEMS) {
				if (item instanceof IConfigurable) {
					if (((IConfigurable) item).isEnabled()) {
						registry.register(item);
					}
				} else {
					registry.register(item);
				}
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelRegister(ModelRegistryEvent event) {
		for (Item item : ItemsSNS.ITEMS) {
			if (item instanceof IConfigurable) {
				if (((IConfigurable) item).isEnabled()) {
					registerItemRenderer(item, 0, "inventory");
				}
			} else {
				registerItemRenderer(item, 0, "inventory");
			}
		}
	}
	
	@SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event) {
		
        // LEATHER ITEMS
        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(ItemsSNS.UNFINISHED_LEATHER_SACK), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ").setRegistryName("unfinished_leather_sack")
        );
    }
	
	public static void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}
}