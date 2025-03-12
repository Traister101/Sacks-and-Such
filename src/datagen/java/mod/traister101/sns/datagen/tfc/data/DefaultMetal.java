package mod.traister101.sns.datagen.tfc.data;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Metal.*;

import net.minecraft.world.level.material.Fluid;

public enum DefaultMetal implements MetalData {
	BISMUTH(0.14F, 270, Default.BISMUTH),
	BISMUTH_BRONZE(0.35F, 985, Default.BISMUTH_BRONZE),
	BLACK_BRONZE(0.35F, 1070, Default.BLACK_BRONZE),
	BRONZE(0.35F, 950, Default.BRONZE),
	BRASS(0.35F, 930, Default.BRASS),
	COPPER(0.35F, 1080, Default.COPPER),
	GOLD(0.6F, 1060, Default.GOLD),
	NICKEL(0.48F, 1453, Default.NICKEL),
	ROSE_GOLD(0.35F, 960, Default.ROSE_GOLD),
	SILVER(0.48F, 961, Default.SILVER),
	TIN(0.14F, 230, Default.TIN),
	ZINC(0.21F, 420, Default.ZINC),
	STERLING_SILVER(0.35F, 950, Default.STERLING_SILVER),
	WROUGHT_IRON(0.35F, 1535, Default.CAST_IRON),
	CAST_IRON(0.35F, 1535, Default.CAST_IRON),
	PIG_IRON(0.35F, 1535, Default.CAST_IRON),
	STEEL(0.35F, 1540, Default.STEEL),
	BLACK_STEEL(0.35F, 1485, Default.BLACK_STEEL),
	BLUE_STEEL(0.35F, 1540, Default.BLUE_STEEL),
	RED_STEEL(0.35F, 1540, Default.RED_STEEL),
	WEAK_STEEL(0.35F, 1540, Default.WEAK_STEEL),
	WEAK_BLUE_STEEL(0.35F, 1540, Default.WEAK_BLUE_STEEL),
	WEAK_RED_STEEL(0.35F, 1540, Default.WEAK_RED_STEEL),
	HIGH_CARBON_STEEL(0.35F, 1540, Default.PIG_IRON),
	HIGH_CARBON_BLACK_STEEL(0.35F, 1540, Default.WEAK_STEEL),
	HIGH_CARBON_BLUE_STEEL(0.35F, 1540, Default.WEAK_BLUE_STEEL),
	HIGH_CARBON_RED_STEEL(0.35F, 1540, Default.WEAK_RED_STEEL),
	UNKNOWN_METAL(0.5F, 400, Default.UNKNOWN);

	public final float meltTemp;
	private final Default tfcMetal;
	private final float baseHeatCapacity;

	DefaultMetal(final float baseHeatCapacity, final float meltTemp, final Default tfcMetal) {
		this.tfcMetal = tfcMetal;
		this.baseHeatCapacity = baseHeatCapacity;
		this.meltTemp = meltTemp;
	}

	@Override
	public float getMeltTemp() {
		return this.meltTemp;
	}

	@Override
	public float specificHeatCapacity() {
		return MetalData.specificHeatCapacity(baseHeatCapacity);
	}

	@Override
	public float ingotHeatCapacity() {
		return MetalData.ingotHeatCapacity(baseHeatCapacity);
	}

	@Override
	public Tier metalTier() {
		return tfcMetal.metalTier();
	}

	@Override
	public Fluid meltMetal() {
		return TFCFluids.METALS.get(tfcMetal).getSource();
	}
}