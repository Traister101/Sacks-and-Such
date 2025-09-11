package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.attribute.SNSAttributes;
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
import java.util.function.Supplier;

public class HorseshoesItem extends Item {

	public static final String LAST_STEP_NBT_KEY = "lastStep";
	public static final String LAST_STEP_X_NBT_KEY = "x";
	public static final String LAST_STEP_Z_NBT_KEY = "z";
	public static final String STEPS_NBT_KEY = "steps";

	public static final UUID HORSE_SHOE_UUID = UUID.fromString("de872635-2298-412b-beac-667462412c28");
	public static final String HORSESHOE_MODIFIER_TOOLTIP = SacksNSuch.MODID + ".tooltip.horseshoe.modifier";

	private final Supplier<Integer> stepsPerDamage;
	private final Supplier<Double> movementSpeed;
	private final Supplier<Double> bonusFallDistance;
	private final Supplier<Double> bonusStepDistance;
	@Getter(lazy = true)
	private final Multimap<Attribute, AttributeModifier> attributeModifiers = Util.make(() -> {
		final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(HorseshoesItem.HORSE_SHOE_UUID, "Horseshoe movement speed bonus", movementSpeed.get(),
						Operation.MULTIPLY_TOTAL));
		builder.put(SNSAttributes.EXTRA_FALL_DISTANCE.get(),
				new AttributeModifier(HORSE_SHOE_UUID, "Horseshoe fall distance bonus", bonusFallDistance.get(), Operation.ADDITION));
		builder.put(ForgeMod.STEP_HEIGHT_ADDITION.get(),
				new AttributeModifier(HORSE_SHOE_UUID, "Horseshoe step bonus", bonusStepDistance.get(), Operation.ADDITION));
		return builder.build();
	});

	public HorseshoesItem(final Properties properties, final Supplier<Integer> stepsPerDamage, final Supplier<Double> movementSpeed,
			final Supplier<Double> bonusFallDistance, final Supplier<Double> bonusStepDistance) {
		super(properties);
		this.stepsPerDamage = stepsPerDamage;
		this.movementSpeed = movementSpeed;
		this.bonusFallDistance = bonusFallDistance;
		this.bonusStepDistance = bonusStepDistance;
	}

	public HorseshoesItem(final Properties properties, final HorseshoesProperties horseshoesProperties) {
		this(properties, horseshoesProperties.stepsPerDamage(), horseshoesProperties.movementSpeed(), horseshoesProperties.bonusFallDistance(),
				horseshoesProperties.bonusStepDistance());
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

		if (getSteps(itemStack) > stepsPerDamage.get()) {
			itemStack.hurtAndBreak(1, horse, e -> e.broadcastBreakEvent(EquipmentSlot.FEET));
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrCreateTagElement(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (horse.onGround() && !horse.isPassenger()) {
			if (stepsPerDamage.get() > 0 && (lastX != horse.xOld || lastZ != horse.zOld)) {
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

		Supplier<Integer> stepsPerDamage();

		Supplier<Double> movementSpeed();

		Supplier<Double> bonusFallDistance();

		Supplier<Double> bonusStepDistance();
	}
}