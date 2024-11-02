package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.datagen.tfc.data.*;

import net.minecraft.data.PackOutput;

public class BuiltInItemHeats extends ItemHeatProvider {

	public BuiltInItemHeats(final PackOutput output) {
		super(output, SacksNSuch.MODID);
	}

	@Override
	protected void addHeats() {
		ingotHeat(SNSItems.BUCKLE.get(), DefaultMetal.WROUGHT_IRON, 100);

		ingotHeat(SNSItems.STEEL_HORSESHOE.get(), DefaultMetal.STEEL, 100);
		ingotHeat(SNSItems.BLACK_STEEL_HORSESHOE.get(), DefaultMetal.BLACK_STEEL, 100);
		ingotHeat(SNSItems.RED_STEEL_HORSESHOE.get(), DefaultMetal.RED_STEEL, 100);
		ingotHeat(SNSItems.BLUE_STEEL_HORSESHOE.get(), DefaultMetal.BLUE_STEEL, 100);

		ingotHeat(SNSItems.STEEL_HORSESHOES.get(), DefaultMetal.STEEL, 400);
		ingotHeat(SNSItems.BLACK_STEEL_HORSESHOES.get(), DefaultMetal.BLACK_STEEL, 400);
		ingotHeat(SNSItems.RED_STEEL_HORSESHOES.get(), DefaultMetal.RED_STEEL, 400);
		ingotHeat(SNSItems.BLUE_STEEL_HORSESHOES.get(), DefaultMetal.BLUE_STEEL, 400);
	}
}