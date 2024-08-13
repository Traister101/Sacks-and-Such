package mod.traister101.sacks.common;

import net.dries007.tfc.util.Helpers;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SNSTags {

	@UtilityClass
	public final class Items {

		/**
		 * TFCs small ore tag for its ore items
		 */
		public static final TagKey<Item> TFC_ORE = fromTFC("ore_pieces");

		/**
		 * TFCs foods tag
		 */
		public static final TagKey<Item> TFC_FOODS = fromTFC("foods");

		/**
		 * Tag for items that shouldn't go inside sacks. We only put our sacks in this
		 */
		public static final TagKey<Item> PREVENTED_IN_SACKS = create("prevented_in_sacks");

		public static final TagKey<Item> ALLOWED_IN_SEED_POUCH = create("allowed_in_seed_pouch");

		private static TagKey<Item> fromTFC(final String name) {
			return TagKey.create(Registries.ITEM, Helpers.identifier(name));
		}

		@SuppressWarnings("SameParameterValue")
		private static TagKey<Item> create(final String name) {
			return TagKey.create(Registries.ITEM, new ResourceLocation(SacksNSuch.MODID, name));
		}
	}
}