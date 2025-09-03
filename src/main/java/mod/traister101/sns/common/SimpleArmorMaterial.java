package mod.traister101.sns.common;

import net.dries007.tfc.util.PhysicalDamageType.Multiplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.*;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import lombok.*;
import lombok.Builder.Default;
import java.util.function.Supplier;

@Builder
@ToString
public class SimpleArmorMaterial implements ArmorMaterial, Multiplier {

	@Getter
	private final ResourceLocation id;
	@Default
	private final Supplier<SoundEvent> equipSound = () -> SoundEvents.ARMOR_EQUIP_GENERIC;
	private final int feetDurability;
	private final int legDurability;
	private final int chestDurability;
	private final int headDurability;
	private final int feetDefense;
	private final int legDefense;
	private final int chestDefense;
	private final int headDefense;
	private final int enchantability;
	private final float toughness;
	private final float knockbackResistance;
	private final float crushingModifier;
	private final float piercingModifier;
	private final float slashingModifier;
	@Default
	private final Supplier<Ingredient> repairIngredient = () -> Ingredient.EMPTY;

	public static SimpleArmorMaterialBuilder copy(final ArmorMaterial armorMaterial) {
		final var builder = builder();
		builder.equipSound(armorMaterial::getEquipSound)
				.feetDurability(armorMaterial.getDurabilityForType(Type.BOOTS))
				.legDurability(armorMaterial.getDurabilityForType(Type.LEGGINGS))
				.chestDurability(armorMaterial.getDurabilityForType(Type.CHESTPLATE))
				.headDurability(armorMaterial.getDurabilityForType(Type.HELMET))
				.feetDefense(armorMaterial.getDefenseForType(Type.BOOTS))
				.legDefense(armorMaterial.getDefenseForType(Type.LEGGINGS))
				.chestDefense(armorMaterial.getDefenseForType(Type.CHESTPLATE))
				.headDefense(armorMaterial.getDefenseForType(Type.HELMET))
				.enchantability(armorMaterial.getEnchantmentValue())
				.toughness(armorMaterial.getToughness())
				.repairIngredient(armorMaterial::getRepairIngredient);
		if (armorMaterial instanceof Multiplier multiplier) {
			builder.crushingModifier(multiplier.crushing()).piercingModifier(multiplier.piercing()).slashingModifier(multiplier.slashing());
		}

		return builder;
	}

	@Override
	public int getDurabilityForType(final Type type) {
		return switch (type) {
			case BOOTS -> feetDurability;
			case LEGGINGS -> legDurability;
			case CHESTPLATE -> chestDurability;
			case HELMET -> headDurability;
		};
	}

	@Override
	public int getDefenseForType(final Type type) {
		return switch (type) {
			case BOOTS -> feetDefense;
			case LEGGINGS -> legDefense;
			case CHESTPLATE -> chestDefense;
			case HELMET -> headDefense;
		};
	}

	@Override
	public int getEnchantmentValue() {
		return enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return equipSound.get();
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repairIngredient.get();
	}

	@Override
	public String getName() {
		return id.toString();
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

	public static class SimpleArmorMaterialBuilder {

		public SimpleArmorMaterialBuilder rawEquipSound(final SoundEvent equipSound) {
			return equipSound(() -> equipSound);
		}
	}
}