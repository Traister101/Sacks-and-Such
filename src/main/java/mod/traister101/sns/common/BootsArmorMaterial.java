package mod.traister101.sns.common;

import mod.traister101.sns.SacksNSuch;
import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.util.PhysicalDamageType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.*;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Locale;

public enum BootsArmorMaterial implements ArmorMaterial, PhysicalDamageType.Multiplier {
	HIKING_BOOTS(420, 1, 15, 0, 0, 0, 15, 10),
	STEEL_TOE_HIKING_BOOTS(TFCArmorMaterials.STEEL),
	BLACK_STEEL_TOE_HIKING_BOOTS(TFCArmorMaterials.BLACK_STEEL),
	BLUE_STEEL_TOE_HIKING_BOOTS(TFCArmorMaterials.BLUE_STEEL),
	RED_STEEL_TOE_HIKING_BOOTS(TFCArmorMaterials.RED_STEEL);

	private final ResourceLocation serializedName;
	private final int durability;
	private final int defence;
	private final int enchantability;
	private final float toughness;
	private final float knockbackResistance;
	private final float crushingModifier;
	private final float piercingModifier;
	private final float slashingModifier;

	BootsArmorMaterial(final int durability, final int defence, final int enchantability, final float toughness, final float knockbackResistance,
			final float crushingModifier, final float piercingModifier, final float slashingModifier) {
		this.serializedName = new ResourceLocation(SacksNSuch.MODID, this.name().toLowerCase(Locale.ROOT));
		this.durability = durability;
		this.defence = defence;
		this.enchantability = enchantability;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.crushingModifier = crushingModifier * 0.25F;
		this.piercingModifier = piercingModifier * 0.25F;
		this.slashingModifier = slashingModifier * 0.25F;
	}

	BootsArmorMaterial(final TFCArmorMaterials tfcArmorMaterials) {
		this(tfcArmorMaterials.getDurabilityForType(Type.BOOTS), tfcArmorMaterials.getDefenseForType(Type.BOOTS),
				tfcArmorMaterials.getEnchantmentValue(), tfcArmorMaterials.getToughness(), tfcArmorMaterials.getKnockbackResistance(),
				tfcArmorMaterials.crushing(), tfcArmorMaterials.piercing(), tfcArmorMaterials.slashing());
	}

	@Override
	public int getDurabilityForType(final Type type) {
		return durability;
	}

	@Override
	public int getDefenseForType(final Type type) {
		return defence;
	}

	@Override
	public int getEnchantmentValue() {
		return enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_LEATHER;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.EMPTY;
	}

	@Override
	@Deprecated
	public String getName() {
		return serializedName.toString();
	}

	@Override
	public float getToughness() {
		return toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	@Override
	public float crushing() {
		return crushingModifier;
	}

	@Override
	public float piercing() {
		return piercingModifier;
	}

	@Override
	public float slashing() {
		return slashingModifier;
	}
}
