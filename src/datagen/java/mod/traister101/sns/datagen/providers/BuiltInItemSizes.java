package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.datagen.tfc.data.ItemSizeProvider;
import net.dries007.tfc.common.capabilities.size.*;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;

public class BuiltInItemSizes extends ItemSizeProvider {

	public BuiltInItemSizes(final PackOutput output) {
		super(output, SacksNSuch.MODID);
	}

	@Override
	protected void addSizes() {
		itemSize(SNSItems.UNFINISHED_LEATHER_SACK, Weight.MEDIUM, Size.SMALL);
		itemSize(SNSItems.REINFORCED_FIBER, Weight.VERY_LIGHT, Size.SMALL);
		itemSize(SNSItems.REINFORCED_FABRIC, Weight.LIGHT, Size.SMALL);
		itemSize(SNSItems.PACK_FRAME, Weight.HEAVY, Size.LARGE);
		itemSize(SNSItems.LEATHER_STRIP, Weight.VERY_LIGHT, Size.VERY_SMALL);
		itemSize(SNSItems.BOUND_LEATHER_STRIP, Weight.VERY_LIGHT, Size.VERY_SMALL);
		itemSize(SNSItems.BUCKLE, Weight.LIGHT, Size.SMALL);

		// Horseshoes (single)
		size("horseshoe", Ingredient.of(SNSItems.STEEL_HORSESHOE.get(), SNSItems.BLACK_STEEL_HORSESHOE.get(), SNSItems.BLUE_STEEL_HORSESHOE.get(),
				SNSItems.RED_STEEL_HORSESHOE.get()), Weight.LIGHT, Size.NORMAL);
		// Horseshoes (all 4)
		size("horseshoes", Ingredient.of(SNSItems.STEEL_HORSESHOES.get(), SNSItems.BLACK_STEEL_HORSESHOES.get(), SNSItems.BLUE_STEEL_HORSESHOES.get(),
				SNSItems.RED_STEEL_HORSESHOES.get()), Weight.MEDIUM, Size.NORMAL);
	}
}