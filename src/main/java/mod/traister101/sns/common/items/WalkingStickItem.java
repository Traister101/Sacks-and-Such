package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.config.entries.WalkingStickConfig;
import mod.traister101.sns.util.SNSUtils;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class WalkingStickItem extends Item {

	public static final UUID WALKING_STICK_UUID = UUID.fromString("b5085e07-0ed2-4dc4-abdc-256e58ff51cd");

	private final WalkingStickProperties walkingStickProperties;
	@Getter(value = AccessLevel.PRIVATE, lazy = true)
	private final Multimap<Attribute, AttributeModifier> mainHandModifiers = Util.make(() -> {
		final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(WALKING_STICK_UUID, "Walking stick main hand movement bonus", walkingStickProperties.mainHandSpeedBonus(),
						Operation.MULTIPLY_TOTAL));
		return builder.build();
	});
	@Getter(value = AccessLevel.PRIVATE, lazy = true)
	private final Multimap<Attribute, AttributeModifier> offHandModifiers = Util.make(() -> {
		final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(WALKING_STICK_UUID, "Walking stick off hand movement bonus", walkingStickProperties.offHandSpeedBonus(),
						Operation.MULTIPLY_TOTAL));
		return builder.build();
	});

	public WalkingStickItem(final Properties properties, final WalkingStickProperties walkingStickProperties) {
		super(properties);
		this.walkingStickProperties = walkingStickProperties;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlot slot, final ItemStack stack) {
		return switch (slot) {
			case MAINHAND -> getMainHandModifiers();
			case OFFHAND -> getOffHandModifiers();
			default -> super.getAttributeModifiers(slot, stack);
		};
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> components,
			final TooltipFlag isAdvanced) {
		components.add(Component.translatable(SNSUtils.PREVENT_SLOW_TOOLTIP));
		super.appendHoverText(itemStack, level, components, isAdvanced);
	}

	public interface WalkingStickProperties {

		static WalkingStickProperties fromConfig(final WalkingStickConfig config) {
			return new WalkingStickProperties() {
				@Override
				public double mainHandSpeedBonus() {
					return config.mainHandSpeedBonus.get();
				}

				@Override
				public double offHandSpeedBonus() {
					return config.offHandSpeedBonus.get();
				}
			};
		}

		double mainHandSpeedBonus();

		double offHandSpeedBonus();
	}
}