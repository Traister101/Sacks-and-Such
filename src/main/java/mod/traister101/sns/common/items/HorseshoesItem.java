package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.config.ServerConfig.HorseshoesConfig;

import net.minecraft.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Supplier;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public class HorseshoesItem extends Item {

	public static final String LAST_STEP_NBT_KEY = "lastStep";
	public static final String LAST_STEP_X_NBT_KEY = "x";
	public static final String LAST_STEP_Z_NBT_KEY = "z";
	public static final String STEPS_NBT_KEY = "steps";

	public static final UUID HORSE_SHOE_UUID = UUID.fromString("de872635-2298-412b-beac-667462412c28");
	public static final String HORSESHOE_MODIFIER_TOOLTIP = SacksNSuch.MODID + ".tooltip.horseshoe.modifier";

	private final Supplier<Double> movementSpeed;
	private final Supplier<Double> bonusFallDistance;
	@Getter(lazy = true)
	private final Multimap<Attribute, AttributeModifier> attributeModifiers = Util.make(() -> {
		final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(HorseshoesItem.HORSE_SHOE_UUID, "Horseshoe movement speed bonus", movementSpeed.get(),
						Operation.MULTIPLY_TOTAL));
		builder.put(SNSAttributes.EXTRA_FALL_DISTANCE.get(),
				new AttributeModifier(HORSE_SHOE_UUID, "Horseshoe fall distance bonus", bonusFallDistance.get(), Operation.ADDITION));
		return builder.build();
	});

	public HorseshoesItem(final Properties properties, final Supplier<Double> movementSpeed, final Supplier<Double> bonusFallDistance) {
		super(properties);
		this.movementSpeed = movementSpeed;
		this.bonusFallDistance = bonusFallDistance;
	}

	public HorseshoesItem(final Properties properties, final HorseshoesConfig horseshoesConfig) {
		this(properties, horseshoesConfig.movementSpeed, horseshoesConfig.bonusFallDistance);
	}

	public static int getSteps(final ItemStack itemStack) {
		return itemStack.getOrCreateTag().getInt(STEPS_NBT_KEY);
	}

	public static void setSteps(final ItemStack itemStack, final int steps) {
		itemStack.getOrCreateTag().putInt(STEPS_NBT_KEY, steps);
	}

	public static void horseshoeTick(final ItemStack itemStack, final Level level, final AbstractHorse horse) {
		if (level.isClientSide) return;

		if (getSteps(itemStack) > SNSConfig.SERVER.horseshoesStepsPerDamage.get()) {
			itemStack.hurtAndBreak(1, horse, e -> e.broadcastBreakEvent(EquipmentSlot.FEET));
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrCreateTagElement(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (horse.onGround() && !horse.isPassenger()) {
			if (SNSConfig.SERVER.horseshoesStepsPerDamage.get() > 0 && (lastX != horse.xOld || lastZ != horse.zOld)) {
				setSteps(itemStack, getSteps(itemStack) + 1);
				lastStep.putDouble("x", horse.xOld);
				lastStep.putDouble("z", horse.zOld);
			}
		}
	}

	public static int getHorseshoesSlot(final AbstractHorse horse) {
		return horse.canWearArmor() ? 2 : 1;
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> tooltip,
			final TooltipFlag tooltipFlag) {
		final var modifiers = this.getAttributeModifiers();
		if (modifiers.isEmpty()) return;

		tooltip.add(Component.translatable(HORSESHOE_MODIFIER_TOOLTIP).withStyle(ChatFormatting.GRAY));

		for (final var entry : modifiers.entries()) {
			final var modifier = entry.getValue();
			final var amount = modifier.getAmount();

			final double displayAmount;
			if (modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
				if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
					displayAmount = amount * 10;
				} else {
					displayAmount = amount;
				}
			} else {
				displayAmount = amount * 100;
			}

			if (amount > 0) {
				tooltip.add(Component.translatable("attribute.modifier.plus." + modifier.getOperation().toValue(),
								ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount), Component.translatable(entry.getKey().getDescriptionId()))
						.withStyle(ChatFormatting.BLUE));
			} else if (amount < 0) {
				tooltip.add(Component.translatable("attribute.modifier.take." + modifier.getOperation().toValue(),
								ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount * -1), Component.translatable(entry.getKey().getDescriptionId()))
						.withStyle(ChatFormatting.RED));
			}
		}
	}
}