package mod.traister101.sns.datagen.recipes;

import com.google.common.collect.*;
import com.google.gson.*;
import mod.traister101.sns.datagen.recipes.CraftingRecipeBuilder.ShapedCraftingRecipeBuilder.DamageInputShaped;
import mod.traister101.sns.datagen.recipes.CraftingRecipeBuilder.ShapelessCraftingRecipeBuilder.DamageInputShapeless;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;

import net.minecraft.advancements.*;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class AdvancedCraftingRecipeBuilder<B extends AdvancedCraftingRecipeBuilder<B>> extends
		net.minecraft.data.recipes.CraftingRecipeBuilder implements RecipeBuilder {

	protected final String folderName;
	protected final CraftingBookCategory craftingBookCategory;
	protected final Item result;
	protected final int count;
	protected final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	protected final List<ItemStackModifier> modifiers = new ArrayList<>();
	@Nullable
	protected String group;
	protected boolean damageInputs;

	protected AdvancedCraftingRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result,
			final int count) {
		this.craftingBookCategory = craftingBookCategory;
		this.folderName = folderName;
		this.result = result.asItem();
		this.count = count;
	}

	public static AdvancedShapedRecipeBuilder shaped(final ItemLike result) {
		return shaped("crafting", result);
	}

	public static AdvancedShapedRecipeBuilder shaped(final ItemLike result, final int count) {
		return shaped("crafting", result, count);
	}

	public static AdvancedShapedRecipeBuilder shaped(final String folderName, final ItemLike result) {
		return shaped(folderName, result, 1);
	}

	public static AdvancedShapedRecipeBuilder shaped(final String folderName, final ItemLike result, final int count) {
		return new AdvancedShapedRecipeBuilder(CraftingBookCategory.MISC, folderName, result, count);
	}

	public static AdvancedShapelessRecipeBuilder shapeless(final ItemLike result) {
		return shapeless("crafting", result);
	}

	public static AdvancedShapelessRecipeBuilder shapeless(final ItemLike result, final int count) {
		return shapeless("crafting", result, count);
	}

	public static AdvancedShapelessRecipeBuilder shapeless(final String folderName, final ItemLike result) {
		return shapeless(folderName, result, 1);
	}

	public static AdvancedShapelessRecipeBuilder shapeless(final String folderName, final ItemLike result, final int count) {
		return new AdvancedShapelessRecipeBuilder(CraftingBookCategory.MISC, folderName, result, count);
	}

	public B modifier(final ItemStackModifier stackModifier) {
		modifiers.add(stackModifier);
		return self();
	}

	@Override
	public B unlockedBy(final String criterionName, final CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(criterionName, criterionTrigger);
		return self();
	}

	@Override
	public B group(@Nullable final String groupName) {
		group = groupName;
		return self();
	}

	@Override
	public Item getResult() {
		return result;
	}

	@Override
	public void save(final Consumer<FinishedRecipe> finishedRecipeConsumer, final ResourceLocation recipeId) {
		ensureValid(recipeId);
		advancement.parent(ROOT_RECIPE_ADVANCEMENT)
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.requirements(RequirementsStrategy.OR);
		finishedRecipeConsumer.accept(createRecipe(recipeId));
	}

	@Override
	public void save(final Consumer<FinishedRecipe> finishedRecipeConsumer) {
		save(finishedRecipeConsumer, RecipeBuilder.getDefaultRecipeId(getResult()).withPrefix(folderName + "/"));
	}

	/**
	 * Creates a {@link net.dries007.tfc.common.recipes.DamageInputsCraftingRecipe} when set
	 */
	public B damageInputs() {
		damageInputs = true;
		return self();
	}

	/**
	 * Makes sure that this recipe is valid and obtainable.
	 */
	protected void ensureValid(final ResourceLocation recipeId) {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	protected abstract B self();

	protected abstract FinishedRecipe createRecipe(final ResourceLocation recipeId);

	public static final class AdvancedShapedRecipeBuilder extends AdvancedCraftingRecipeBuilder<AdvancedShapedRecipeBuilder> {

		private final List<String> rows = Lists.newArrayList();
		private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
		private boolean showNotification = true;
		private int inputRow;
		private int inputColumn;

		private AdvancedShapedRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result,
				final int count) {
			super(craftingBookCategory, folderName, result, count);
		}

		public AdvancedShapedRecipeBuilder inputItem(final Character symbol, final TagKey<Item> tag, final int row, final int column) {
			return inputItem(symbol, Ingredient.of(tag), row, column);
		}

		public AdvancedShapedRecipeBuilder inputItem(final Character symbol, final ItemLike item, final int row, final int column) {
			return inputItem(symbol, Ingredient.of(item), row, column);
		}

		public AdvancedShapedRecipeBuilder inputItem(final Character symbol, final Ingredient ingredient, final int row, final int column) {
			inputRow = row;
			inputColumn = column;
			return define(symbol, ingredient);
		}

		/**
		 * Adds a key to the recipe pattern.
		 */
		public AdvancedShapedRecipeBuilder define(final Character symbol, final TagKey<Item> tag) {
			return define(symbol, Ingredient.of(tag));
		}

		/**
		 * Adds a key to the recipe pattern.
		 */
		public AdvancedShapedRecipeBuilder define(final Character symbol, final ItemLike item) {
			return define(symbol, Ingredient.of(item));
		}

		/**
		 * Adds a key to the recipe pattern.
		 */
		public AdvancedShapedRecipeBuilder define(final Character symbol, final Ingredient ingredient) {
			if (key.containsKey(symbol)) throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");

			if (symbol == ' ') throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");

			key.put(symbol, ingredient);
			return this;
		}

		/**
		 * Adds a new row to the pattern for this recipe.
		 */
		public AdvancedShapedRecipeBuilder pattern(final String pattern) {
			if (!rows.isEmpty() && pattern.length() != rows.get(0).length()) {
				throw new IllegalArgumentException("Pattern must be the same width on every line!");
			}

			rows.add(pattern);
			return this;
		}

		/**
		 * Adds multiple rows to the pattern for this recipe.
		 */
		public AdvancedShapedRecipeBuilder pattern(final String... pattern) {
			Arrays.stream(pattern).forEach(this::pattern);
			return this;
		}

		public AdvancedShapedRecipeBuilder showNotification(final boolean showNotification) {
			this.showNotification = showNotification;
			return this;
		}

		@Override
		protected AdvancedShapedRecipeBuilder self() {
			return this;
		}

		@Override
		protected FinishedRecipe createRecipe(final ResourceLocation recipeId) {
			final AdvancedShapedResult recipe = new AdvancedShapedResult(recipeId, this.result, count, group == null ? "" : group,
					craftingBookCategory, rows, key, advancement, recipeId.withPrefix("recipes/" + folderName + "/"), showNotification, modifiers,
					inputRow, inputColumn);
			return damageInputs ? new DamageInputShaped(recipe) : recipe;
		}

		private static final class AdvancedShapedResult extends CraftingResult {

			private final ResourceLocation id;
			private final Item result;
			private final int count;
			private final String group;
			private final List<String> pattern;
			private final Map<Character, Ingredient> key;
			private final Advancement.Builder advancement;
			private final ResourceLocation advancementId;
			private final boolean showNotification;
			private final List<ItemStackModifier> modifiers;
			private final int inputRow;
			private final int inputColumn;

			public AdvancedShapedResult(final ResourceLocation recipeId, final Item result, final int count, final String group,
					final CraftingBookCategory craftingBookCategory, final List<String> pattern, final Map<Character, Ingredient> key,
					final Advancement.Builder advancement, final ResourceLocation advancementId, final boolean showNotification,
					final List<ItemStackModifier> modifiers, final int inputRow, final int inputColumn) {
				super(craftingBookCategory);
				this.id = recipeId;
				this.result = result;
				this.count = count;
				this.group = group;
				this.pattern = pattern;
				this.key = key;
				this.advancement = advancement;
				this.advancementId = advancementId;
				this.showNotification = showNotification;
				this.modifiers = modifiers;
				this.inputRow = inputRow;
				this.inputColumn = inputColumn;
			}

			@Override
			public void serializeRecipeData(final JsonObject jsonObject) {
				super.serializeRecipeData(jsonObject);
				if (!group.isEmpty()) {
					jsonObject.addProperty("group", group);
				}

				{
					final var pattern = new JsonArray();
					this.pattern.forEach(pattern::add);
					jsonObject.add("pattern", pattern);
				}

				{
					final var key = new JsonObject();
					this.key.forEach((character, ingredient) -> key.add(String.valueOf(character), ingredient.toJson()));
					jsonObject.add("key", key);
				}

				jsonObject.add("result", ItemStackModifier.writeItemStackProvider(result, count, modifiers));

				jsonObject.addProperty("show_notification", showNotification);

				jsonObject.addProperty("input_row", inputRow);
				jsonObject.addProperty("input_column", inputColumn);
			}

			@Override
			public ResourceLocation getId() {
				return this.id;
			}

			@Override
			public RecipeSerializer<?> getType() {
				return TFCRecipeSerializers.ADVANCED_SHAPED_CRAFTING.get();
			}

			@Override
			public JsonObject serializeAdvancement() {
				return advancement.serializeToJson();
			}

			@Override
			public ResourceLocation getAdvancementId() {
				return this.advancementId;
			}
		}
	}

	public static final class AdvancedShapelessRecipeBuilder extends AdvancedCraftingRecipeBuilder<AdvancedShapelessRecipeBuilder> {

		private final List<Ingredient> ingredients = Lists.newArrayList();
		@Nullable
		private Ingredient primaryIngredient;

		private AdvancedShapelessRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result,
				final int count) {
			super(craftingBookCategory, folderName, result, count);
		}

		public AdvancedShapelessRecipeBuilder primaryIngredient(final Ingredient primaryIngredient) {
			this.primaryIngredient = primaryIngredient;
			return this;
		}

		/**
		 * Adds an ingredient that can be any item in the given tag.
		 */
		public AdvancedShapelessRecipeBuilder requires(final TagKey<Item> tag) {
			return requires(Ingredient.of(tag));
		}

		/**
		 * Adds an ingredient of the given item.
		 */
		public AdvancedShapelessRecipeBuilder requires(final ItemLike item) {
			return requires(Ingredient.of(item));
		}

		/**
		 * Adds the given ingredient multiple times.
		 */
		public AdvancedShapelessRecipeBuilder requires(final ItemLike item, final int quantity) {
			for (int i = 0; i < quantity; ++i) requires(item);
			return this;
		}

		/**
		 * Adds an ingredient.
		 */
		public AdvancedShapelessRecipeBuilder requires(final Ingredient ingredient) {
			ingredients.add(ingredient);
			return this;
		}

		/**
		 * Adds an ingredient multiple times.
		 */
		public AdvancedShapelessRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
			for (int i = 0; i < quantity; ++i) requires(ingredient);

			return this;
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
			if (primaryIngredient == null) {
				throw new IllegalStateException("No primary ingredient set");
			}
		}

		@Override
		protected AdvancedShapelessRecipeBuilder self() {
			return this;
		}

		@Override
		protected FinishedRecipe createRecipe(final ResourceLocation recipeId) {
			assert primaryIngredient != null : "How has this happened?";
			final var recipe = new AdvancedResult(recipeId, this.result, count, group == null ? "" : group, craftingBookCategory, ingredients,
					advancement, recipeId.withPrefix("recipes/" + folderName + "/"), modifiers, primaryIngredient);

			return damageInputs ? new DamageInputShapeless(recipe) : recipe;
		}

		public static final class AdvancedResult extends CraftingRecipeBuilder.CraftingResult {

			private final ResourceLocation id;
			private final Item result;
			private final int count;
			private final String group;
			private final List<Ingredient> ingredients;
			private final Advancement.Builder advancement;
			private final ResourceLocation advancementId;
			private final List<ItemStackModifier> modifiers;
			private final Ingredient primaryIngredient;

			public AdvancedResult(final ResourceLocation id, final Item result, final int count, final String group,
					final CraftingBookCategory category, final List<Ingredient> ingredients, final Builder advancement,
					final ResourceLocation advancementId, final List<ItemStackModifier> modifiers, final Ingredient primaryIngredient) {
				super(category);
				this.id = id;
				this.result = result;
				this.count = count;
				this.group = group;
				this.ingredients = ingredients;
				this.advancement = advancement;
				this.advancementId = advancementId;
				this.modifiers = modifiers;
				this.primaryIngredient = primaryIngredient;
			}

			@Override
			public void serializeRecipeData(final JsonObject jsonObject) {
				super.serializeRecipeData(jsonObject);

				if (!group.isEmpty()) jsonObject.addProperty("group", group);
				{
					final var ingredients = new JsonArray();
					this.ingredients.stream().map(Ingredient::toJson).forEach(ingredients::add);
					jsonObject.add("ingredients", ingredients);
				}

				{
					final var result = new JsonObject();
					{
						final var stack = new JsonObject();
						//noinspection DataFlowIssue
						stack.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
						if (count > 1) stack.addProperty("count", count);
						result.add("stack", stack);
						jsonObject.add("result", result);
					}

					{
						final var modifiers = new JsonArray();
						this.modifiers.forEach(stackModifier -> modifiers.add(stackModifier.toJson()));
						result.add("modifiers", modifiers);
					}
					jsonObject.add("result", result);
				}

				jsonObject.add("primary_ingredient", primaryIngredient.toJson());
			}

			@Override
			public ResourceLocation getId() {
				return this.id;
			}

			@Override
			public RecipeSerializer<?> getType() {
				return TFCRecipeSerializers.ADVANCED_SHAPELESS_CRAFTING.get();
			}

			@Override
			public JsonObject serializeAdvancement() {
				return this.advancement.serializeToJson();
			}

			@Override
			public ResourceLocation getAdvancementId() {
				return this.advancementId;
			}
		}
	}
}