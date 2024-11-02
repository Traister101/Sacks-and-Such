package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.config.SNSConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeMod;

import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class HikingBootsItem extends ArmorItem {

	public static final String LAST_STEP_NBT_KEY = "lastStep";
	public static final String LAST_STEP_X_NBT_KEY = "x";
	public static final String LAST_STEP_Z_NBT_KEY = "z";
	public static final String STEPS_NBT_KEY = "steps";

	public static final String PREVENT_SLOW_TOOLTIP = SacksNSuch.MODID + ".tooltip.hiking_boots.prevents_slow";

	private static final UUID HIKING_BOOTS_UUID = UUID.fromString("1498ff98-5730-4216-a827-857c81e2e12c");

	private final Supplier<Double> movementSpeed;
	private final Supplier<Double> stepHeight;
	private Multimap<Attribute, AttributeModifier> attributeModifiers;

	/**
	 * @param armorMaterial The armor material
	 * @param movementSpeed A supplier for the movement speed modifier the boots provide.
	 * You are expected to pass in a {@link DoubleValue} to facilitate configurability
	 * @param stepHeight A supplier for the step height modifier the boots provide.
	 * You are expected to pass in a {@link DoubleValue} to facilitate configurability
	 */
	public HikingBootsItem(final Properties properties, final ArmorMaterial armorMaterial, final Supplier<Double> movementSpeed,
			final Supplier<Double> stepHeight) {
		super(armorMaterial, Type.BOOTS, properties);
		this.movementSpeed = movementSpeed;
		this.stepHeight = stepHeight;
	}

	public static int getSteps(final ItemStack itemStack) {
		return itemStack.getOrCreateTag().getInt(STEPS_NBT_KEY);
	}

	public static void setSteps(final ItemStack itemStack, final int steps) {
		itemStack.getOrCreateTag().putInt(STEPS_NBT_KEY, steps);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlot slot, final ItemStack itemStack) {
		// Delay attrabute init until server config is loaded :/
		if (attributeModifiers == null) {
			final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
			builder.putAll(super.getAttributeModifiers(slot, itemStack));

			if (0 < movementSpeed.get()) {
				builder.put(Attributes.MOVEMENT_SPEED,
						new AttributeModifier(HIKING_BOOTS_UUID, "Movement Speed", movementSpeed.get(), Operation.MULTIPLY_TOTAL));
			}
			if (0 < stepHeight.get()) {
				builder.put(ForgeMod.STEP_HEIGHT_ADDITION.get(),
						new AttributeModifier(HIKING_BOOTS_UUID, "Step Height", stepHeight.get(), Operation.ADDITION));
			}

			this.attributeModifiers = builder.build();
		}

		return slot == EquipmentSlot.FEET ? attributeModifiers : super.getAttributeModifiers(slot, itemStack);
	}

	@Override
	public void onArmorTick(final ItemStack itemStack, final Level level, final Player player) {
		if (level.isClientSide) return;

		if (getSteps(itemStack) > SNSConfig.SERVER.bootsStepPerDamage.get()) {
			itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.FEET));
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrCreateTagElement(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (player.onGround() && !player.isPassenger() && !player.isCreative()) {
			if (0 < SNSConfig.SERVER.bootsStepPerDamage.get() && (lastX != player.xOld || lastZ != player.zOld)) {
				setSteps(itemStack, getSteps(itemStack) + 1);
				lastStep.putDouble("x", player.xOld);
				lastStep.putDouble("z", player.zOld);
			}
		}
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> components,
			final TooltipFlag tooltipFlag) {
		components.add(Component.translatable(PREVENT_SLOW_TOOLTIP));
		super.appendHoverText(itemStack, level, components, tooltipFlag);
	}
}