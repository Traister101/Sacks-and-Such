package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.config.entries.SnowShoesConfig;
import mod.traister101.sns.util.SNSUtils;

import net.minecraft.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class SnowShoesItem extends Item implements Equipable {

	public static final UUID SNOW_SHOES_UUID = UUID.fromString("b392c19f-2de8-4721-9d91-36c9ef3341b1");

	public static final String PREVENT_SLOW_TOOLTIP = SacksNSuch.MODID + ".tooltip.snowshoes.prevents_slow";
	public static final String SNOW_MODIFIER_TOOLTIP = SacksNSuch.MODID + ".tooltip.snowshoes.snow_modifier";
	public static final String NON_SNOW_MODIFIER_TOOLTIP = SacksNSuch.MODID + ".tooltip.snowshoes.non_snow_modifier";

	public static final String SNOW_NAME = "Snow Shoes snow speed bonus";
	public static final String NON_SNOW_NAME = "Snow Shoes non-snow speed penalty";

	@Getter
	private final SnowShoesProperties snowShoesProperties;
	@Getter(value = AccessLevel.PRIVATE, lazy = true)
	private final Multimap<Attribute, AttributeModifier> snowAttributes = Util.make(() -> {
		final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(SNOW_SHOES_UUID, SNOW_NAME, this.snowShoesProperties.speedBonus(), Operation.MULTIPLY_TOTAL));
		return builder.build();
	});

	@Getter(value = AccessLevel.PRIVATE, lazy = true)
	private final Multimap<Attribute, AttributeModifier> nonSnowAttributes = Util.make(() -> {
		final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(SNOW_SHOES_UUID, NON_SNOW_NAME, this.snowShoesProperties.speedPenalty(), Operation.MULTIPLY_TOTAL));
		return builder.build();
	});

	public SnowShoesItem(final Properties properties, final SnowShoesProperties snowShoesProperties) {
		super(properties);
		this.snowShoesProperties = snowShoesProperties;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand usedHand) {
		return swapWithEquipmentSlot(this, level, player, usedHand);
	}

	@Override
	public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag isAdvanced) {
		tooltip.add(Component.translatable(PREVENT_SLOW_TOOLTIP));

		final var snowAttributes = this.getSnowAttributes();
		if (!snowAttributes.isEmpty()) {
			tooltip.add(Component.translatable(SNOW_MODIFIER_TOOLTIP).withStyle(ChatFormatting.GRAY));

			SNSUtils.attributeTooltips(tooltip, snowAttributes);
		}

		final var nonSnowAttributes = this.getNonSnowAttributes();
		if (!nonSnowAttributes.isEmpty()) {
			tooltip.add(Component.translatable(NON_SNOW_MODIFIER_TOOLTIP).withStyle(ChatFormatting.GRAY));

			SNSUtils.attributeTooltips(tooltip, nonSnowAttributes);
		}
	}

	@Override
	public boolean canWalkOnPowderedSnow(final ItemStack stack, final LivingEntity wearer) {
		return true;
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.FEET;
	}

	public interface SnowShoesProperties {

		static SnowShoesProperties fromConfig(final SnowShoesConfig config) {
			return new SnowShoesProperties() {
				@Override
				public double speedBonus() {
					return config.speedBonus.get();
				}

				@Override
				public double speedPenalty() {
					return config.speedPenalty.get();
				}
			};
		}

		double speedBonus();

		double speedPenalty();
	}
}