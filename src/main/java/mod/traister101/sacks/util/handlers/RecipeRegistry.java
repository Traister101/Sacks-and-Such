package mod.traister101.sacks.util.handlers;

import static mod.traister101.sacks.SacksNSuch.MODID;

import mod.traister101.sacks.objects.items.ItemsSNS;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = MODID)
public final class RecipeRegistry {

	@SubscribeEvent
	public static void onRegisterKnappingRecipeEvent(Register<KnappingRecipe> event) {
		IForgeRegistry<KnappingRecipe> recipe = event.getRegistry();
		// LEATHER ITEMS
		recipe.register(new KnappingRecipeSimple(KnappingType.LEATHER, true, 
				new ItemStack(ItemsSNS.UNFINISHED_LEATHER_SACK), 
				" XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ")
				.setRegistryName("unfinished_leather_sack"));
	}
}