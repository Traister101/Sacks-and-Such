package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.config.entries.HorseshoesConfig;
import mod.traister101.sns.util.SNSUtils;

import net.minecraft.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.ForgeMod;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class HorseshoesItem extends Item {

	public static final String LAST_STEP_NBT_KEY = "lastStep";
	public static final String LAST_STEP_X_NBT_KEY = "x";
	public static final String LAST_STEP_Z_NBT_KEY = "z";
	public static final String STEPS_NBT_KEY = "steps";

	public static final UUID HORSE_SHOE_UUID = UUID.fromString("de872635-2298-412b-beac-667462412c28");
	public static final String HORSESHOE_MODIFIER_TOOLTIP = SacksNSuch.MODID + ".tooltip.horseshoe.modifier";

	private final HorseshoesProperties horseshoesProperties;
	@Getter(lazy = true)
	private final Multimap<Attribute, AttributeModifier> attributeModifiers = Util.make(() -> {
		final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(HorseshoesItem.HORSE_SHOE_UUID, "Horseshoe movement speed bonus", horseshoesProperties.movementSpeed(),
						Operation.MULTIPLY_TOTAL));
		builder.put(SNSAttributes.EXTRA_FALL_DISTANCE.get(),
				new AttributeModifier(HORSE_SHOE_UUID, "Horseshoe fall distance bonus", horseshoesProperties.bonusFallDistance(),
						Operation.ADDITION));
		builder.put(ForgeMod.STEP_HEIGHT_ADDITION.get(),
				new AttributeModifier(HORSE_SHOE_UUID, "Horseshoe step bonus", horseshoesProperties.bonusStepDistance(), Operation.ADDITION));
		return builder.build();
	});

	public HorseshoesItem(final Properties properties, final HorseshoesProperties horseshoesProperties) {
		super(properties);
		this.horseshoesProperties = horseshoesProperties;
	}

	public static int getSteps(final ItemStack itemStack) {
		return itemStack.getOrCreateTag().getInt(STEPS_NBT_KEY);
	}

	public static void setSteps(final ItemStack itemStack, final int steps) {
		itemStack.getOrCreateTag().putInt(STEPS_NBT_KEY, steps);
	}

	public static int getHorseshoesSlot(final AbstractHorse horse) {
		return horse.canWearArmor() ? 2 : 1;
	}

	public void horseshoeTick(final ItemStack itemStack, final Level level, final AbstractHorse horse) {
		if (level.isClientSide) return;

		if (getSteps(itemStack) > horseshoesProperties.stepsPerDamage()) {
			itemStack.hurtAndBreak(1, horse, e -> e.broadcastBreakEvent(EquipmentSlot.FEET));
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrCreateTagElement(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (horse.onGround() && !horse.isPassenger()) {
			if (horseshoesProperties.stepsPerDamage() > 0 && (lastX != horse.xOld || lastZ != horse.zOld)) {
				setSteps(itemStack, getSteps(itemStack) + 1);
				lastStep.putDouble("x", horse.xOld);
				lastStep.putDouble("z", horse.zOld);
			}
		}
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> tooltip,
			final TooltipFlag tooltipFlag) {
		final var modifiers = this.getAttributeModifiers();
		if (modifiers.isEmpty()) return;

		tooltip.add(Component.translatable(HORSESHOE_MODIFIER_TOOLTIP).withStyle(ChatFormatting.GRAY));

		SNSUtils.attributeTooltips(tooltip, modifiers);
	}

	public interface HorseshoesProperties {

		static HorseshoesProperties fromConfig(final HorseshoesConfig config) {
			return new HorseshoesProperties() {

				@Override
				public int stepsPerDamage() {
					return config.stepsPerDamage.get();
				}

				@Override
				public double movementSpeed() {
					return config.movementSpeed.get();
				}

				@Override
				public double bonusFallDistance() {
					return config.bonusFallDistance.get();
				}

				@Override
				public double bonusStepDistance() {
					return config.bonusStepDistance.get();
				}
			};
		}

		int stepsPerDamage();

		double movementSpeed();

		double bonusFallDistance();

		double bonusStepDistance();
	}
}