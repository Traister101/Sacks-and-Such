package mod.traister101.sns.datagen.recipes;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;

import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluid;

import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Consumer;

public final class HeatingRecipe implements FinishedRecipe {

	private final ResourceLocation recipeId;
	private final Ingredient input;
	private final float temperature;
	@Nullable
	private final Item resultItem;
	private final int itemCount;
	@Nullable
	private final Fluid resultFluid;
	private final int fluidAmount;
	private final List<ItemStackModifier> modifiers = new ArrayList<>();

	private HeatingRecipe(final ResourceLocation recipeId, final Ingredient input, final float temperature, @Nullable final Item resultItem,
			final int itemCount, @Nullable final Fluid resultFluid, final int fluidAmount) {
		this.recipeId = recipeId.withPrefix("heating/");
		this.input = input;
		this.temperature = temperature;
		this.resultItem = resultItem;
		this.itemCount = itemCount;
		this.resultFluid = resultFluid;
		this.fluidAmount = fluidAmount;
	}

	public static HeatingRecipe destory(final Item input, final float temperature) {
		return destory(RecipeBuilder.getDefaultRecipeId(input), Ingredient.of(input), temperature);
	}

	public static HeatingRecipe destory(final ResourceLocation recipeId, final Ingredient input, final float temperature) {
		return new HeatingRecipe(recipeId, input, temperature, null, 0, null, 0);
	}

	public static HeatingRecipe cook(final ResourceLocation recipeId, final Ingredient input, final float temperature, final Item result) {
		return cook(recipeId, input, temperature, result, 0);
	}

	public static HeatingRecipe cook(final ResourceLocation recipeId, final Ingredient input, final float temperature, final Item result,
			final int count) {
		return new HeatingRecipe(recipeId, input, temperature, result, count, null, 0);
	}

	public static HeatingRecipe melt(final Item input, final float temperature, final Fluid fluid, final int amount) {
		return melt(RecipeBuilder.getDefaultRecipeId(input), Ingredient.of(input), temperature, fluid, amount);
	}

	public static HeatingRecipe melt(final ResourceLocation recipeId, final Ingredient input, final float temperature, final Fluid fluid,
			final int amount) {
		return new HeatingRecipe(recipeId, input, temperature, null, 0, fluid, amount);
	}

	public HeatingRecipe modifier(final ItemStackModifier stackModifier) {
		if (resultItem == null) throw new IllegalStateException("This recipe doesn't have an item result");
		modifiers.add(stackModifier);
		return this;
	}

	@Override
	public void serializeRecipeData(final JsonObject recipe) {
		recipe.add("ingredient", input.toJson());
		if (resultItem != null) recipe.add("result_item", ItemStackModifier.writeItemStackProvider(resultItem, itemCount, modifiers));

		if (resultFluid != null) {
			final var fluidResult = new JsonObject();
			fluidResult.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(resultFluid)).toString());
			if (fluidAmount != 1_000) fluidResult.addProperty("amount", fluidAmount);
			recipe.add("result_fluid", fluidResult);
		}

		recipe.addProperty("temperature", temperature);
	}

	@Override
	public ResourceLocation getId() {
		return recipeId;
	}

	@Override
	public RecipeSerializer<?> getType() {
		return TFCRecipeSerializers.HEATING.get();
	}

	@Nullable
	@Override
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementId() {
		return null;
	}

	public void save(final Consumer<FinishedRecipe> writer) {
		writer.accept(this);
	}
}