package mod.traister101.sacks.util;

import static mod.traister101.sacks.SacksNSuch.MODID;

import javax.annotation.Nonnull;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.objects.items.ItemsSNS;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = MODID)
public final class RecipeRegistry {

	@SubscribeEvent
	public static void onRegisterKnappingRecipeEvent(Register<KnappingRecipe> event) {
		IForgeRegistry<KnappingRecipe> recipeRegistry = event.getRegistry();
		
		if (ConfigSNS.LEATHERSACK.isEnabled)
		leatherKnappingRecipe(recipeRegistry, ItemsSNS.UNFINISHED_LEATHER_SACK, 1,
				" XXX ",
				"XXXXX",
				"XXXXX",
				"XXXXX",
				" XXX ");
		
//		clayKnappingRecipe(recipeRegistry, ItemsSNS.UNFIRED_TINY_VESSEL, 4,
//				" XXX ",
//				"XX XX",
//				"XXXXX",
//				"XX XX",
//				" XXX ");
		
		if (ConfigSNS.EXPLOSIVE_VESSEL.isEnabled)
		fireClayKnappingRecipe(recipeRegistry, ItemsSNS.UNFIRED_EXPLOSIVE_VESSEL, 1,
				" XXX ",
				"XXXXX",
				"XXXXX",
				"XXXXX",
				" XXX ");
	}
	
	@SubscribeEvent
	public static void onRegisterHeatRecipeEvent(Register<HeatRecipe> event) {
		IForgeRegistry<HeatRecipe> recipeRegistry = event.getRegistry();
		
		heatRecipe(recipeRegistry, ItemsSNS.UNFIRED_TINY_VESSEL, ItemsSNS.FIRED_TINY_VESSEL);
		heatRecipe(recipeRegistry, ItemsSNS.UNFIRED_EXPLOSIVE_VESSEL, ItemsSNS.EXPLOSIVE_VESSEL);
	}
	
	private static void heatRecipe (IForgeRegistry<HeatRecipe> recipeRegistry, @Nonnull Item itemInput, @Nonnull Item itemOutput) {
		recipeRegistry.register(new HeatRecipeSimple(IIngredient.of(itemInput), new ItemStack(itemOutput), 1599f, Metal.Tier.TIER_I).setRegistryName(itemInput.getRegistryName()));
	}
	
	private static void leatherKnappingRecipe(IForgeRegistry<KnappingRecipe> recipeRegistry, @Nonnull Item item, int count, String... pattern) {
		recipeRegistry.register(new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(item, count), pattern).setRegistryName(item.getRegistryName()));
	}
	
	private static void clayKnappingRecipe(IForgeRegistry<KnappingRecipe> recipeRegistry, @Nonnull Item item, int count, String... pattern) {
		recipeRegistry.register(new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(item, count), pattern).setRegistryName(item.getRegistryName()));
	}
	
	private static void fireClayKnappingRecipe(IForgeRegistry<KnappingRecipe> recipeRegistry, @Nonnull Item item, int count, String... pattern) {
		recipeRegistry.register(new KnappingRecipeSimple(KnappingType.FIRE_CLAY, true, new ItemStack(item, count), pattern).setRegistryName(item.getRegistryName()));
	}
}