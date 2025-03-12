package mod.traister101.sns.datagen.recipes;

import com.google.common.collect.*;
import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;

import net.minecraft.advancements.*;
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
public abstract class CraftingRecipeBuilder<B extends CraftingRecipeBuilder<B>> extends net.minecraft.data.recipes.CraftingRecipeBuilder implements
		RecipeBuilder {

	protected final String folderName;
	protected final CraftingBookCategory craftingBookCategory;
	protected final Item result;
	protected final int count;
	protected final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	@Nullable
	protected String group;
	protected boolean damageInputs;

	protected CraftingRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result,
			final int count) {
		this.craftingBookCategory = craftingBookCategory;
		this.folderName = folderName;
		this.result = result.asItem();
		this.count = count;
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result) {
		return shapeless("crafting", result);
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result, final int count) {
		return shapeless("crafting", result, count);
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final String folderName, final ItemLike result) {
		return shapeless(folderName, result, 1);
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final String folderName, final ItemLike result, final int count) {
		return new ShapelessCraftingRecipeBuilder(CraftingBookCategory.MISC, folderName, result, count);
	}

	public static ShapedCraftingRecipeBuilder shaped(final ItemLike result) {
		return shaped("crafting", result);
	}

	public static ShapedCraftingRecipeBuilder shaped(final ItemLike result, final int count) {
		return shaped("crafting", result, count);
	}

	public static ShapedCraftingRecipeBuilder shaped(final String folderName, final ItemLike result) {
		return shaped(folderName, result, 1);
	}

	public static ShapedCraftingRecipeBuilder shaped(final String folderName, final ItemLike result, final int count) {
		return new ShapedCraftingRecipeBuilder(CraftingBookCategory.MISC, folderName, result, count);
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

	public static final class ShapedCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapedCraftingRecipeBuilder> {

		private final List<String> rows = Lists.newArrayList();
		private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
		private boolean showNotification = true;

		private ShapedCraftingRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result,
				final int count) {
			super(craftingBookCategory, folderName, result, count);
		}

		/**
		 * Adds a key to the recipe pattern.
		 */
		public ShapedCraftingRecipeBuilder define(final Character symbol, final TagKey<Item> tag) {
			return define(symbol, Ingredient.of(tag));
		}

		/**
		 * Adds a key to the recipe pattern.
		 */
		public ShapedCraftingRecipeBuilder define(final Character symbol, final ItemLike item) {
			return define(symbol, Ingredient.of(item));
		}

		/**
		 * Adds a key to the recipe pattern.
		 */
		public ShapedCraftingRecipeBuilder define(final Character symbol, final Ingredient ingredient) {
			if (key.containsKey(symbol)) {
				throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
			}

			if (symbol == ' ') {
				throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
			}

			key.put(symbol, ingredient);
			return self();
		}

		/**
		 * Adds a new row to the pattern for this recipe.
		 */
		public ShapedCraftingRecipeBuilder pattern(final String pattern) {
			if (!rows.isEmpty() && pattern.length() != rows.get(0).length()) {
				throw new IllegalArgumentException("Pattern must be the same width on every line!");
			}

			rows.add(pattern);
			return self();
		}

		/**
		 * Adds multiple rows to the pattern for this recipe.
		 */
		public ShapedCraftingRecipeBuilder pattern(final String... pattern) {
			Arrays.stream(pattern).forEach(this::pattern);
			return self();
		}

		public ShapedCraftingRecipeBuilder showNotification(final boolean showNotification) {
			this.showNotification = showNotification;
			return self();
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);

			if (rows.isEmpty()) throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");

			final Set<Character> set = Sets.newHashSet(key.keySet());
			set.remove(' ');

			for (final String pattern : rows) {
				for (int i = 0; i < pattern.length(); ++i) {
					final char symbol = pattern.charAt(i);
					if (!key.containsKey(symbol) && symbol != ' ') {
						throw new IllegalStateException("Pattern in recipe " + recipeId + " uses undefined symbol '" + symbol + "'");
					}

					set.remove(symbol);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + recipeId);
			}

			if (rows.size() == 1 && rows.get(0).length() == 1) {
				throw new IllegalStateException(
						"Shaped recipe " + recipeId + " only takes in a single item - should it be a shapeless recipe instead?");
			}
		}

		@Override
		protected ShapedCraftingRecipeBuilder self() {
			return this;
		}

		@Override
		protected FinishedRecipe createRecipe(final ResourceLocation recipeId) {
			final var recipe = new ShapedRecipeBuilder.Result(recipeId, result, count, group == null ? "" : group, craftingBookCategory, rows, key,
					advancement, recipeId.withPrefix("recipes/" + folderName + "/"), showNotification);
			return damageInputs ? new DamageInputShaped(recipe) : recipe;
		}

		public static final class DamageInputShaped implements FinishedRecipe {

			private final FinishedRecipe finishedRecipe;

			public DamageInputShaped(final FinishedRecipe finishedRecipe) {
				this.finishedRecipe = finishedRecipe;
			}

			@Override
			public void serializeRecipeData(final JsonObject jsonObject) {
				final JsonObject recipe = new JsonObject();
				//noinspection DataFlowIssue
				recipe.addProperty("type", ForgeRegistries.RECIPE_SERIALIZERS.getKey(finishedRecipe.getType()).toString());
				finishedRecipe.serializeRecipeData(recipe);
				jsonObject.add("recipe", recipe);
			}

			@Override
			public ResourceLocation getId() {
				return finishedRecipe.getId();
			}

			@Override
			public RecipeSerializer<?> getType() {
				return TFCRecipeSerializers.DAMAGE_INPUT_SHAPED_CRAFTING.get();
			}

			@Nullable
			@Override
			public JsonObject serializeAdvancement() {
				return finishedRecipe.serializeAdvancement();
			}

			@Nullable
			@Override
			public ResourceLocation getAdvancementId() {
				return finishedRecipe.getAdvancementId();
			}
		}
	}

	public static final class ShapelessCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapelessCraftingRecipeBuilder> {

		private final List<Ingredient> ingredients = Lists.newArrayList();

		private ShapelessCraftingRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result,
				final int count) {
			super(craftingBookCategory, folderName, result, count);
		}

		/**
		 * Adds an ingredient that can be any item in the given tag.
		 */
		public ShapelessCraftingRecipeBuilder requires(final TagKey<Item> tag) {
			return requires(Ingredient.of(tag));
		}

		/**
		 * Adds an ingredient of the given item.
		 */
		public ShapelessCraftingRecipeBuilder requires(final ItemLike item) {
			return requires(Ingredient.of(item));
		}

		/**
		 * Adds the given ingredient multiple times.
		 */
		public ShapelessCraftingRecipeBuilder requires(final ItemLike item, final int quantity) {
			for (int i = 0; i < quantity; ++i) requires(item);
			return this;
		}

		/**
		 * Adds an ingredient.
		 */
		public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient) {
			ingredients.add(ingredient);
			return this;
		}

		/**
		 * Adds an ingredient multiple times.
		 */
		public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
			for (int i = 0; i < quantity; ++i) requires(ingredient);

			return this;
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
			if (ingredients.isEmpty()) throw new IllegalStateException("Recipe must have at least 1 ingredient");
		}

		@Override
		protected ShapelessCraftingRecipeBuilder self() {
			return this;
		}

		@Override
		protected FinishedRecipe createRecipe(final ResourceLocation recipeId) {
			final var recipe = new ShapelessRecipeBuilder.Result(recipeId, this.result, count, group == null ? "" : group, craftingBookCategory,
					ingredients, advancement, recipeId.withPrefix("recipes/" + folderName + "/"));

			return damageInputs ? new DamageInputShapeless(recipe) : recipe;
		}

		public static final class DamageInputShapeless implements FinishedRecipe {

			private final FinishedRecipe finishedRecipe;

			public DamageInputShapeless(final FinishedRecipe finishedRecipe) {
				this.finishedRecipe = finishedRecipe;
			}

			@Override
			public void serializeRecipeData(final JsonObject jsonObject) {
				final JsonObject recipe = new JsonObject();
				//noinspection DataFlowIssue
				recipe.addProperty("type", ForgeRegistries.RECIPE_SERIALIZERS.getKey(finishedRecipe.getType()).toString());
				finishedRecipe.serializeRecipeData(recipe);
				jsonObject.add("recipe", recipe);
			}

			@Override
			public ResourceLocation getId() {
				return finishedRecipe.getId();
			}

			@Override
			public RecipeSerializer<?> getType() {
				return TFCRecipeSerializers.DAMAGE_INPUTS_SHAPELESS_CRAFTING.get();
			}

			@Nullable
			@Override
			public JsonObject serializeAdvancement() {
				return finishedRecipe.serializeAdvancement();
			}

			@Nullable
			@Override
			public ResourceLocation getAdvancementId() {
				return finishedRecipe.getAdvancementId();
			}
		}
	}
}