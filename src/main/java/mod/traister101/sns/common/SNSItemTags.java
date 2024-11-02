package mod.traister101.sns.common;

import mod.traister101.sns.SacksNSuch;
import net.dries007.tfc.util.Helpers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SNSItemTags {

	/**
	 * TFCs small ore tag for its ore items
	 */
	public static final TagKey<Item> TFC_ORE_PIECES = fromTFC("ore_pieces");
	/**
	 * TFCs small ore tag for its small ore "nugget" items
	 */
	public static final TagKey<Item> TFC_SMALL_ORE_PIECES = fromTFC("small_ore_pieces");

	/**
	 * TFCs seed tag
	 */
	public static final TagKey<Item> TFC_SEEDS = fromTFC("seeds");

	/**
	 * TFCs soups tag
	 */
	public static final TagKey<Item> TFC_SOUPS = fromTFC("soups");

	/**
	 * TFCs salads tag
	 */
	public static final TagKey<Item> TFC_SALADS = fromTFC("salads");

	/**
	 * TFCs bread tag
	 */
	public static final TagKey<Item> TFC_BREADS = fromTFC("foods/breads");

	/**
	 * TFCs cooked meat tag
	 */
	public static final TagKey<Item> TFC_COOKED_MEATS = fromTFC("foods/cooked_meats");

	/**
	 * TFCs dairy tag
	 */
	public static final TagKey<Item> TFC_DAIRY = fromTFC("foods/dairy");

	/**
	 * TFCs fruit tag
	 */
	public static final TagKey<Item> TFC_FIRUITS = fromTFC("foods/fruits");

	/**
	 * TFCs vegetable tag
	 */
	public static final TagKey<Item> TFC_VEGETABLES = fromTFC("foods/vegetables");

	/**
	 * Tag for items that shouldn't go inside sacks. We only put our sacks in this
	 */
	public static final TagKey<Item> PREVENTED_IN_ITEM_CONTAINERS = create("prevented_in_item_containers");

	public static final TagKey<Item> ALLOWED_IN_SEED_POUCH = create("allowed_in_seed_pouch");

	public static final TagKey<Item> ALLOWED_IN_ORE_SACK = create("allowed_in_ore_sack");

	/**
	 * Tag for lunchbox food. Includes most TFC foods but not some like soups
	 */
	public static final TagKey<Item> LUNCHBOX_FOOD = create("lunchbox_food");

	private static TagKey<Item> fromTFC(final String name) {
		return TagKey.create(Registries.ITEM, Helpers.identifier(name));
	}

	@SuppressWarnings("SameParameterValue")
	private static TagKey<Item> create(final String name) {
		return TagKey.create(Registries.ITEM, new ResourceLocation(SacksNSuch.MODID, name));
	}
}