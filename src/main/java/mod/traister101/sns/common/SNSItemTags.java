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
	 * TFCs javelin tag
	 */
	public static final TagKey<Item> TFC_JAVELINS = fromTFC("javelins");

	/**
	 * TFCs high quality cloth tag
	 */
	public static final TagKey<Item> TFC_HIGH_QUALITY_CLOTH = fromTFC("high_quality_cloth");

	/**
	 * Tag for items that shouldn't go inside Item Containers. We put common items in here like our other item containers
	 */
	public static final TagKey<Item> PREVENTED_IN_ITEM_CONTAINERS = create("prevented_in_item_containers");
	/**
	 * Tag for items that shouldn't go inside Straw Baskets
	 */
	public static final TagKey<Item> PREVENTED_IN_STRAW_BASKET = create("prevented_in_straw_basket");
	/**
	 * Tag for items that shouldn't go inside Leather Sacks
	 */
	public static final TagKey<Item> PREVENTED_IN_LEATHER_SACK = create("prevented_in_leather_sack");
	/**
	 * Tag for items that shouldn't go inside Burlap Sacks
	 */
	public static final TagKey<Item> PREVENTED_IN_BURLAP_SACK = create("prevented_in_burlap_sack");
	/**
	 * Tag for items that shouldn't go inside Ore Sacks
	 */
	public static final TagKey<Item> PREVENTED_IN_ORE_SACK = create("prevented_in_ore_sack");
	/**
	 * Tag for items that shouldn't go inside Seed Pouches
	 */
	public static final TagKey<Item> PREVENTED_IN_SEED_POUCH = create("prevented_in_seed_pouch");
	/**
	 * Tag for items that shouldn't go inside Frame Packs
	 */
	public static final TagKey<Item> PREVENTED_IN_FRAME_PACK = create("prevented_in_frame_pack");
	/**
	 * Tag for items that shouldn't go inside Lunchboxes
	 */
	public static final TagKey<Item> PREVENTED_IN_LUNCHBOX = create("prevented_in_lunchbox");
	/**
	 * Tag for items that shouldn't go inside Quivers
	 */
	public static final TagKey<Item> PREVENTED_IN_QUIVER = create("prevented_in_quiver");

	public static final TagKey<Item> ALLOWED_IN_SEED_POUCH = create("allowed_in_seed_pouch");

	public static final TagKey<Item> ALLOWED_IN_ORE_SACK = create("allowed_in_ore_sack");

	public static final TagKey<Item> ALLOWED_IN_QUIVER = create("allowed_in_quiver");

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