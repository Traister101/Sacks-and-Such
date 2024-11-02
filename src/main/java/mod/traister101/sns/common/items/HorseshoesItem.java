package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.network.*;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.network.PacketDistributor;

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
	public final Supplier<Double> horseshoeSpeedModifier;

	public HorseshoesItem(final Properties properties, final Supplier<Double> horseshoeSpeedModifier) {
		super(properties);
		this.horseshoeSpeedModifier = horseshoeSpeedModifier;
	}

	public static int getSteps(final ItemStack itemStack) {
		return itemStack.getOrCreateTag().getInt(STEPS_NBT_KEY);
	}

	public static void setSteps(final ItemStack itemStack, final int steps) {
		itemStack.getOrCreateTag().putInt(STEPS_NBT_KEY, steps);
	}

	public static void horseshoeTick(final ItemStack itemStack, final Level level, final AbstractHorse horse) {
		if (level.isClientSide) return;

		if (getSteps(itemStack) > SNSConfig.SERVER.horseshoeStepsPerDamage.get()) {
			itemStack.hurtAndBreak(1, horse,
					e -> SNSPacketHandler.send(PacketDistributor.TRACKING_ENTITY.with(() -> e), new ClientboundBreakHorseshoePacket(e)));
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrCreateTagElement(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (horse.onGround() && !horse.isPassenger()) {
			if (SNSConfig.SERVER.horseshoeStepsPerDamage.get() > 0 && (lastX != horse.xOld || lastZ != horse.zOld)) {
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
		tooltip.add(Component.translatable(HORSESHOE_MODIFIER_TOOLTIP).withStyle(ChatFormatting.GRAY));

		final double horseshoeSpeed = horseshoeSpeedModifier.get();
		final double horseshoeDisplaySpeed = horseshoeSpeed * 100;

		if (horseshoeSpeed > 0) {
			tooltip.add(Component.translatable("attribute.modifier.plus." + Operation.MULTIPLY_TOTAL.toValue(),
							ATTRIBUTE_MODIFIER_FORMAT.format(horseshoeDisplaySpeed), Component.translatable(Attributes.MOVEMENT_SPEED.getDescriptionId()))
					.withStyle(ChatFormatting.BLUE));
		} else if (horseshoeSpeed < 0) {
			tooltip.add(Component.translatable("attribute.modifier.take." + Operation.MULTIPLY_TOTAL.toValue(),
					ATTRIBUTE_MODIFIER_FORMAT.format(horseshoeDisplaySpeed * -1),
					Component.translatable(Attributes.MOVEMENT_SPEED.getDescriptionId())).withStyle(ChatFormatting.RED));
		}
	}
}