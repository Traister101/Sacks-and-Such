package mod.traister101.sacks.objects.recipes;

import com.google.gson.JsonObject;
import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.util.SNSUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

import static mod.traister101.sacks.SacksNSuch.MODID;

public class SackUpgradeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

		CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.width = recipe.getRecipeWidth();
		primer.height = recipe.getRecipeHeight();
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = recipe.getIngredients();

		return new SackUpgradeRecipe(new ResourceLocation(MODID, "upgrade"), recipe.getRecipeOutput(), primer);
	}

	public static class SackUpgradeRecipe extends ShapedOreRecipe {

		public SackUpgradeRecipe(ResourceLocation group, ItemStack result, CraftingHelper.ShapedPrimer primer) {
			super(group, result, primer);
		}

		@Nonnull
		@Override
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {

			ItemStack newStack = super.getCraftingResult(inv).copy();
			ItemStack oldStack = inv.getStackInSlot(4);

			SackHandler oldHandler = (SackHandler) SNSUtils.getHandler(oldStack);
			SackHandler newHandler = (SackHandler) SNSUtils.getHandler(newStack);
			for (int i = 0; i < oldHandler.getSlots(); i++) {
				newHandler.insertItem(i, oldHandler.getStackInSlot(i), false);
			}
			NBTTagCompound nbt = newHandler.serializeNBT();
			newStack.setTagCompound(nbt);
			return newStack;
		}
	}
}