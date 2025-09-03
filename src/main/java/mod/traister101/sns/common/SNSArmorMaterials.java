package mod.traister101.sns.common;

import mod.traister101.sns.SacksNSuch;
import net.dries007.tfc.common.TFCArmorMaterials;

import net.minecraft.sounds.SoundEvents;

public final class SNSArmorMaterials {

	public static final SimpleArmorMaterial HIKING_BOOTS = SimpleArmorMaterial.builder()
			.id(SacksNSuch.location("hiking_boots"))
			.feetDurability(420)
			.feetDefense(1)
			.enchantability(15)
			.piercingModifier(15 * 0.25F)
			.slashingModifier(10 * 0.25F)
			.build();

	public static final SimpleArmorMaterial STEEL_TOE_HIKING_BOOTS = SimpleArmorMaterial.copy(TFCArmorMaterials.STEEL)
			.id(SacksNSuch.location("steel_toe_hiking_boots"))
			.rawEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
			.build();

	public static final SimpleArmorMaterial BLACK_STEEL_TOE_HIKING_BOOTS = SimpleArmorMaterial.copy(TFCArmorMaterials.STEEL)
			.id(SacksNSuch.location("black_steel_toe_hiking_boots"))
			.rawEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
			.build();

	public static final SimpleArmorMaterial BLUE_STEEL_TOE_HIKING_BOOTS = SimpleArmorMaterial.copy(TFCArmorMaterials.STEEL)
			.id(SacksNSuch.location("blue_steel_toe_hiking_boots"))
			.rawEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
			.build();

	public static final SimpleArmorMaterial RED_STEEL_TOE_HIKING_BOOTS = SimpleArmorMaterial.copy(TFCArmorMaterials.STEEL)
			.id(SacksNSuch.location("red_steel_toe_hiking_boots"))
			.rawEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
			.build();
}