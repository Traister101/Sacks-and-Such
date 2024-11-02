package mod.traister101.sns.datagen.providers;

import com.google.gson.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.*;
import mod.traister101.sns.datagen.recipes.CraftingRecipeBuilder;
import mod.traister101.sns.datagen.recipes.*;
import mod.traister101.sns.datagen.tfc.data.*;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;

import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class BuiltInRecipes extends RecipeProvider {

	public BuiltInRecipes(final PackOutput packOutput) {
		super(packOutput);
	}

	private static void craftingItems(final Consumer<FinishedRecipe> writer) {
		CraftingRecipeBuilder.shaped(SNSItems.REINFORCED_FIBER.get())
				.pattern("JJJ", "SSS", "JJJ")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('S', Tags.Items.STRING)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_string", has(Tags.Items.STRING))
				.save(writer);

		final TagKey<Item> steelRodsTag = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/steel"));
		{
			CraftingRecipeBuilder.shaped(SNSItems.PACK_FRAME.get())
					.pattern("RRR", "R R", "RRR")
					.define('R', steelRodsTag)
					.unlockedBy("has_steel_rod", has(steelRodsTag))
					.save(writer);
		}

		CraftingRecipeBuilder.shapeless(SNSItems.BOUND_LEATHER_STRIP.get())
				.damageInputs()
				.requires(SNSItems.LEATHER_STRIP.get())
				.requires(SNSItems.REINFORCED_FIBER.get())
				.requires(SNSItems.LEATHER_STRIP.get())
				.requires(TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_leather_strip", has(SNSItems.LEATHER_STRIP.get()))
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		writer.accept(new LeatherKnapping(SNSItems.UNFINISHED_LEATHER_SACK.get(), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX "));
		writer.accept(new LeatherKnapping(SNSItems.LEATHER_STRIP.get(), 3, "X X X", "X X X", "X X X", "X X X", "X X X"));
		writer.accept(new Loom(new ItemStackIngredient(Ingredient.of(SNSItems.REINFORCED_FIBER.get()), 16), SNSItems.REINFORCED_FABRIC.get(), 1, 16,
				new ResourceLocation(SacksNSuch.MODID, "loom/reinforced_fabric")));

		writer.accept(new AnvilRecipe(Ingredient.of(TFCItems.METAL_ITEMS.get(Default.WROUGHT_IRON).get(ItemType.INGOT).get()),
				new ItemStack(SNSItems.BUCKLE.get()), Default.WROUGHT_IRON.metalTier().ordinal(),
				new ForgeRule[] {ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST}, true,
				new ResourceLocation(SacksNSuch.MODID, "iron_buckle")));
		HeatingRecipe.melt(SNSItems.BUCKLE.get(), DefaultMetal.WROUGHT_IRON.meltTemp, DefaultMetal.WROUGHT_IRON.meltMetal(), 100).save(writer);
		writer.accept(new AnvilRecipe(Ingredient.of(TFCItems.METAL_ITEMS.get(Default.STEEL).get(ItemType.INGOT).get()),
				new ItemStack(SNSItems.BUCKLE.get()), Default.STEEL.metalTier().ordinal(),
				new ForgeRule[] {ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST}, true,
				new ResourceLocation(SacksNSuch.MODID, "steel_buckle")));

		horseshoeRecipes(writer, SNSItems.STEEL_HORSESHOE.get(), steelRodsTag, DefaultMetal.STEEL);
		final TagKey<Item> blackSteelRods = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/black_steel"));
		horseshoeRecipes(writer, SNSItems.BLACK_STEEL_HORSESHOE.get(), blackSteelRods, DefaultMetal.BLACK_STEEL);
		final TagKey<Item> blueSteelRods = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/blue_steel"));
		horseshoeRecipes(writer, SNSItems.BLUE_STEEL_HORSESHOE.get(), blueSteelRods, DefaultMetal.BLUE_STEEL);
		final TagKey<Item> redSteelRods = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/red_steel"));
		horseshoeRecipes(writer, SNSItems.RED_STEEL_HORSESHOE.get(), redSteelRods, DefaultMetal.RED_STEEL);
	}

	private static void horseshoeRecipes(final Consumer<FinishedRecipe> writer, final Item horseshoe, final TagKey<Item> steelRodsTag,
			final MetalData metal) {
		final Metal.Tier tier = metal.metalTier();
		writer.accept(new AnvilRecipe(Ingredient.of(steelRodsTag), new ItemStack(horseshoe), tier.ordinal(),
				new ForgeRule[] {ForgeRule.BEND_THIRD_LAST, ForgeRule.BEND_SECOND_LAST, ForgeRule.UPSET_LAST}, false));
		HeatingRecipe.melt(horseshoe, metal.getMeltTemp(), metal.meltMetal(), 100).save(writer);
	}

	private static void containerItems(final Consumer<FinishedRecipe> writer) {
		CraftingRecipeBuilder.shaped(SNSItems.STRAW_BASKET.get())
				.damageInputs()
				.pattern("SSS", "T T", " TK")
				.define('S', TFCItems.STRAW.get())
				.define('T', TFCBlocks.THATCH.get())
				.define('K', TFCTags.Items.KNIVES)
				.unlockedBy("has_straw", has(TFCItems.STRAW.get()))
				.unlockedBy("has_thatch", has(TFCBlocks.THATCH.get()))
				.unlockedBy("has_knife", has(TFCTags.Items.KNIVES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.LEATHER_SACK.get())
				.damageInputs()
				.pattern("JJJ", "LUL", " LN")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('L', SNSItems.LEATHER_STRIP.get())
				.define('U', SNSItems.UNFINISHED_LEATHER_SACK.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_leather_strip", has(SNSItems.LEATHER_STRIP.get()))
				.unlockedBy("has_unfinished_sack", has(SNSItems.UNFINISHED_LEATHER_SACK.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.BURLAP_SACK.get())
				.damageInputs()
				.pattern("JJJ", "B B", " BN")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.SEED_POUCH.get())
				.damageInputs()
				.pattern("SSS", "WBW", " WN")
				.define('S', Tags.Items.STRING)
				.define('W', TFCItems.WOOL_CLOTH.get())
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_string", has(Tags.Items.STRING))
				.unlockedBy("has_wool_cloth", has(TFCItems.WOOL_CLOTH.get()))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.ORE_SACK.get())
				.damageInputs()
				.pattern("RRR", "LBL", " LN")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.define('L', Tags.Items.LEATHER)
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_leather", has(Tags.Items.LEATHER))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.FRAME_PACK.get())
				.damageInputs()
				.pattern("LFL", "LPL", " FN")
				.define('P', SNSItems.PACK_FRAME.get())
				.define('F', SNSItems.REINFORCED_FABRIC.get())
				.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_pack_frame", has(SNSItems.PACK_FRAME.get()))
				.unlockedBy("has_reinforced_fabric", has(SNSItems.REINFORCED_FABRIC.get()))
				.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		{
			final var wroughtIronRodsTag = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/wrought_iron"));
			final var wroughtIronSheetsTag = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sheets/wrought_iron"));
			CraftingRecipeBuilder.shaped(SNSItems.LUNCHBOX.get())
					.pattern("RLR", "SFS", " S ")
					.define('R', wroughtIronRodsTag)
					.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
					.define('S', wroughtIronSheetsTag)
					.define('F', SNSItems.REINFORCED_FABRIC.get())
					.unlockedBy("has_wrought_iron_rods", has(wroughtIronRodsTag))
					.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
					.unlockedBy("has_wrought_iron_sheets", has(wroughtIronSheetsTag))
					.unlockedBy("has_reinforced_fabric", has(SNSItems.REINFORCED_FABRIC.get()))
					.save(writer);
		}
	}

	private static void horseshoesRecipes(final Consumer<FinishedRecipe> writer, final HorseshoesItem horseshoes, final Item horseshoe,
			final MetalData metal) {
		CraftingRecipeBuilder.shapeless(horseshoes).requires(horseshoe, 4).unlockedBy("has_horseshoe", has(horseshoe)).save(writer);
		HeatingRecipe.melt(horseshoes, metal.getMeltTemp(), metal.meltMetal(), 400).save(writer);
	}

	private static void safetyToeHikingBoots(final HikingBootsItem hikingBootsItem, final TagKey<Item> metalSheetsTag,
			final Consumer<FinishedRecipe> writer) {
		AdvancedCraftingRecipeBuilder.shaped(hikingBootsItem)
				.pattern("RWR", "LLL", "TBT")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.inputItem('W', SNSItems.BUCKLE.get(), 0, 1)
				.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
				.define('T', metalSheetsTag)
				.define('B', Items.LEATHER_BOOTS)
				.modifier(ItemStackModifiers.COPY_FORGING_BONUS)
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_buckle", has(SNSItems.BUCKLE.get()))
				.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
				.unlockedBy("has_double_steel_sheet", has(metalSheetsTag))
				.unlockedBy("has_leather_boots", has(Items.LEATHER_BOOTS))
				.save(writer);
	}

	@Override
	protected void buildRecipes(final Consumer<FinishedRecipe> writer) {
		craftingItems(writer);
		containerItems(writer);

		CraftingRecipeBuilder.shaped(SNSItems.MOB_NET_ITEM.get())
				.pattern("R R", " R ", "R R")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.save(writer);

		AdvancedCraftingRecipeBuilder.shaped(SNSItems.HIKING_BOOTS.get())
				.pattern("RWR", "LLL", " B ")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.inputItem('W', SNSItems.BUCKLE.get(), 0, 1)
				.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
				.define('B', Items.LEATHER_BOOTS)
				.modifier(ItemStackModifiers.COPY_FORGING_BONUS)
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_buckle", has(SNSItems.BUCKLE.get()))
				.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
				.unlockedBy("has_leather_boots", has(Items.LEATHER_BOOTS))
				.save(writer);

		{
			final var steelSheets = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sheets/steel"));
			safetyToeHikingBoots(SNSItems.STEEL_TOE_HIKING_BOOTS.get(), steelSheets, writer);
			final var blackSteelSheets = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sheets/black_steel"));
			safetyToeHikingBoots(SNSItems.BLACK_STEEL_TOE_HIKING_BOOTS.get(), blackSteelSheets, writer);
			final var blueSteelSheets = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sheets/blue_steel"));
			safetyToeHikingBoots(SNSItems.BLUE_STEEL_TOE_HIKING_BOOTS.get(), blueSteelSheets, writer);
			final var redSteelSheets = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sheets/red_steel"));
			safetyToeHikingBoots(SNSItems.RED_STEEL_TOE_HIKING_BOOTS.get(), redSteelSheets, writer);
		}

		horseshoesRecipes(writer, SNSItems.STEEL_HORSESHOES.get(), SNSItems.STEEL_HORSESHOE.get(), DefaultMetal.STEEL);
		horseshoesRecipes(writer, SNSItems.BLACK_STEEL_HORSESHOES.get(), SNSItems.BLACK_STEEL_HORSESHOE.get(), DefaultMetal.BLACK_STEEL);
		horseshoesRecipes(writer, SNSItems.BLUE_STEEL_HORSESHOES.get(), SNSItems.BLUE_STEEL_HORSESHOE.get(), DefaultMetal.BLUE_STEEL);
		horseshoesRecipes(writer, SNSItems.RED_STEEL_HORSESHOES.get(), SNSItems.RED_STEEL_HORSESHOE.get(), DefaultMetal.RED_STEEL);
	}

	// TODO this is gross
	public static class LeatherKnapping implements FinishedRecipe {

		private final Item result;
		private final int count;
		private final String[] pattern;

		public LeatherKnapping(final Item result, final int count, final String... pattern) {
			this.result = result;
			this.count = count;
			this.pattern = pattern;
		}

		public LeatherKnapping(final Item result, final String... pattern) {
			this(result, 1, pattern);
		}

		@Override
		public void serializeRecipeData(final JsonObject recipe) {
			recipe.addProperty("knapping_type", "tfc:leather");
			final var pattern = new JsonArray();

			Arrays.stream(this.pattern).forEach(pattern::add);

			recipe.add("pattern", pattern);

			final var result = new JsonObject();
			//noinspection DataFlowIssue
			result.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
			if (1 < count) result.addProperty("count", count);
			recipe.add("result", result);
		}

		@Override
		public ResourceLocation getId() {
			//noinspection DataFlowIssue
			return ForgeRegistries.ITEMS.getKey(result).withPrefix("leather_knapping/");
		}

		@Override
		public RecipeSerializer<?> getType() {
			return TFCRecipeSerializers.KNAPPING.get();
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
	}

	public static class Loom implements FinishedRecipe {

		private final ItemStackIngredient ingredient;
		private final Item result;
		private final int count;
		private final int steps;
		private final ResourceLocation texture;

		public Loom(final ItemStackIngredient ingredient, final Item result, final int count, final int steps, final ResourceLocation texture) {
			this.ingredient = ingredient;
			this.result = result;
			this.count = count;
			this.steps = steps;
			this.texture = texture;
		}

		@Override
		public void serializeRecipeData(final JsonObject jsonObject) {
			final var ingredient = new JsonObject();

			ingredient.add("ingredient", this.ingredient.ingredient().toJson());
			ingredient.addProperty("count", this.ingredient.count());

			jsonObject.add("ingredient", ingredient);

			final var result = new JsonObject();
			//noinspection DataFlowIssue
			result.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
			if (this.count > 1) {
				result.addProperty("count", this.count);
			}

			jsonObject.add("result", result);
			jsonObject.addProperty("steps_required", steps);

			jsonObject.addProperty("in_progress_texture", texture.toString());
		}

		@Override
		public ResourceLocation getId() {
			//noinspection DataFlowIssue
			return ForgeRegistries.ITEMS.getKey(result).withPrefix("loom/");
		}

		@Override
		public RecipeSerializer<?> getType() {
			return TFCRecipeSerializers.LOOM.get();
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
	}

	public static class AnvilRecipe implements FinishedRecipe {

		private final Ingredient input;
		private final ItemStack result;
		private final int tier;
		private final ForgeRule[] rules;
		private final boolean applyForgingBonus;
		private final ResourceLocation id;

		public AnvilRecipe(final Ingredient input, final ItemStack result, final int tier, final ForgeRule[] rules, final boolean applyForgingBonus,
				final ResourceLocation id) {
			this.input = input;
			this.result = result;
			this.tier = tier;
			this.rules = rules;
			this.applyForgingBonus = applyForgingBonus;
			this.id = id.withPrefix("anvil/");
		}

		public AnvilRecipe(final Ingredient input, final ItemStack result, final int tier, final ForgeRule[] rules, final boolean applyForgingBonus) {
			this(input, result, tier, rules, applyForgingBonus, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result.getItem())));
		}

		@Override
		public void serializeRecipeData(final JsonObject recipe) {
			recipe.add("input", input.toJson());
			{
				final JsonObject result = new JsonObject();
				//noinspection DataFlowIssue
				result.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result.getItem()).toString());
				if (1 < this.result.getCount()) result.addProperty("count", this.result.getCount());
				recipe.add("result", result);
			}
			recipe.addProperty("tier", tier);

			final var rules = new JsonArray();
			Arrays.stream(this.rules).forEach(forgeRule -> rules.add(forgeRule.name().toLowerCase(Locale.ROOT)));
			recipe.add("rules", rules);

			recipe.addProperty("apply_forging_bonus", applyForgingBonus);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return TFCRecipeSerializers.ANVIL.get();
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
	}
}