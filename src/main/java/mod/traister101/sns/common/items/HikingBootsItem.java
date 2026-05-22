package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.client.models.*;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.config.entries.BootsConfig;
import mod.traister101.sns.util.SNSUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;

import org.jetbrains.annotations.*;
import java.util.*;
import java.util.function.Consumer;

public class HikingBootsItem extends ArmorItem {

	public static final String LAST_STEP_NBT_KEY = "lastStep";
	public static final String LAST_STEP_X_NBT_KEY = "x";
	public static final String LAST_STEP_Z_NBT_KEY = "z";
	public static final String STEPS_NBT_KEY = "steps";
	public static final String DISABLE_STEP_UP_NBT_KEY = "disableStepUp";

	public static final String PREVENT_SLOW_TOOLTIP = SacksNSuch.MODID + ".tooltip.hiking_boots.prevents_slow";
	public static final String STEP_UP_TOOLTIP = SacksNSuch.MODID + ".tooltip.hiking_boots.step_up";

	private static final UUID HIKING_BOOTS_UUID = UUID.fromString("1498ff98-5730-4216-a827-857c81e2e12c");

	private final HikingBootProperties bootProperties;
	private Multimap<Attribute, AttributeModifier> allAttributeModifiers;
	private Multimap<Attribute, AttributeModifier> stepUpDisabledAttributeModifiers;

	public HikingBootsItem(final Properties properties, final ArmorMaterial armorMaterial, final HikingBootProperties bootProperties) {
		super(armorMaterial, Type.BOOTS, properties);
		this.bootProperties = bootProperties;
	}

	public static int getSteps(final ItemStack itemStack) {
		return itemStack.getOrCreateTag().getInt(STEPS_NBT_KEY);
	}

	public static void setSteps(final ItemStack itemStack, final int steps) {
		itemStack.getOrCreateTag().putInt(STEPS_NBT_KEY, steps);
	}

	public static boolean isStepUpEnabled(final ItemStack bootsStack) {
		if (!(bootsStack.getItem() instanceof HikingBootsItem)) return false;

		final var compoundTag = bootsStack.getTag();
		if (compoundTag == null) return true;

		if (!compoundTag.contains(DISABLE_STEP_UP_NBT_KEY, Tag.TAG_BYTE)) return true;

		return !compoundTag.getBoolean(DISABLE_STEP_UP_NBT_KEY);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlot slot, final ItemStack itemStack) {
		if (slot != EquipmentSlot.FEET) return super.getAttributeModifiers(slot, itemStack);
		// Delay attribute init until server config is loaded :/
		if (allAttributeModifiers == null || stepUpDisabledAttributeModifiers == null) {
			final var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
			builder.putAll(super.getAttributeModifiers(slot, itemStack));

			if (0 < bootProperties.movementSpeed()) {
				builder.put(Attributes.MOVEMENT_SPEED,
						new AttributeModifier(HIKING_BOOTS_UUID, "Movement Speed", bootProperties.movementSpeed(), Operation.MULTIPLY_TOTAL));
			}

			if (0 < bootProperties.fallPadding()) {
				builder.put(SNSAttributes.EXTRA_FALL_DISTANCE.get(),
						new AttributeModifier(HIKING_BOOTS_UUID, "Fall Padding", bootProperties.fallPadding(), Operation.ADDITION));
			}

			stepUpDisabledAttributeModifiers = builder.build();

			if (0 < bootProperties.stepHeight()) {
				builder.put(ForgeMod.STEP_HEIGHT_ADDITION.get(),
						new AttributeModifier(HIKING_BOOTS_UUID, "Step Height", bootProperties.stepHeight(), Operation.ADDITION));
			}

			allAttributeModifiers = builder.build();
		}

		return isStepUpEnabled(itemStack) ? allAttributeModifiers : stepUpDisabledAttributeModifiers;
	}

	@Override
	public void onArmorTick(final ItemStack itemStack, final Level level, final Player player) {
		if (level.isClientSide) return;

		if (getSteps(itemStack) > bootProperties.stepsPerDamage()) {
			itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.FEET));
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrCreateTagElement(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (player.onGround() && !player.isPassenger() && !player.isCreative()) {
			if (0 < bootProperties.stepsPerDamage() && (lastX != player.xOld || lastZ != player.zOld)) {
				setSteps(itemStack, getSteps(itemStack) + 1);
				lastStep.putDouble("x", player.xOld);
				lastStep.putDouble("z", player.zOld);
			}
		}
	}

	@Nullable
	@Override
	public String getArmorTexture(final ItemStack itemStack, final Entity entity, final EquipmentSlot slot, final String type) {
		return SacksNSuch.MODID + ":textures/models/armor/hiking_boots.png";
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> components,
			final TooltipFlag tooltipFlag) {
		components.add(Component.translatable(PREVENT_SLOW_TOOLTIP));
		components.add(Component.translatable(STEP_UP_TOOLTIP, SNSUtils.toggleTooltip(isStepUpEnabled(itemStack))).withStyle(ChatFormatting.GRAY));
		super.appendHoverText(itemStack, level, components, tooltipFlag);
	}

	@Override
	public void initializeClient(final Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {

			private final EnumMap<BootModelType, HumanoidModel<?>> models = new EnumMap<>(BootModelType.class);

			@NotNull
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(final LivingEntity livingEntity, final ItemStack itemStack,
					final EquipmentSlot equipmentSlot, final HumanoidModel<?> original) {
				return models.computeIfAbsent(SNSConfig.CLIENT.bootModelType.get(), bootModelType -> {
					final EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
					return switch (bootModelType) {
						case FANCY -> new FancyHikingBootsModel<>(entityModels.bakeLayer(FancyHikingBootsModel.LAYER_LOCATION));
						case NO_FLOOF -> new NoFloofHikingBootsModel<>(entityModels.bakeLayer(NoFloofHikingBootsModel.LAYER_LOCATION));
						case VANILLA -> new VanillaHikingBootsModel<>(entityModels.bakeLayer(VanillaHikingBootsModel.LAYER_LOCATION));
					};
				});
			}
		});
	}

	public enum BootModelType {
		FANCY,
		NO_FLOOF,
		VANILLA
	}

	public interface HikingBootProperties {

		static HikingBootProperties fromConfig(final BootsConfig config) {
			return new HikingBootProperties() {
				@Override
				public int stepsPerDamage() {
					return config.stepsPerDamage.get();
				}

				@Override
				public double movementSpeed() {
					return config.movementSpeed.get();
				}

				@Override
				public double stepHeight() {
					return config.stepHeight.get();
				}

				@Override
				public double fallPadding() {
					return config.fallPadding.get();
				}
			};
		}

		int stepsPerDamage();

		double movementSpeed();

		double stepHeight();

		double fallPadding();
	}
}