package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.traister101.sns.common.items.*;
import mod.traister101.sns.compat.curios.CuriosUtils;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.items.ItemSlot;
import net.dries007.tfc.common.TFCTags.Blocks;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SnowLayerBlock;

import javax.annotation.Nullable;
import java.util.stream.Stream;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	protected LivingEntityMixin(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Shadow
	public abstract ItemStack getItemBySlot(final EquipmentSlot pSlot);

	@Shadow
	@Nullable
	public abstract AttributeInstance getAttribute(final Attribute attribute);

	/**
	 * @reason In order for our Boots to work we need to modify the speed factor accounting for our Boots
	 * @author Traister101
	 */
	@ModifyReturnValue(method = "getBlockSpeedFactor", at = @At("RETURN"))
	private float modifyBlockSpeedFactor(final float original) {
		if (original >= 1) return original;

		final var feetItem = getItemBySlot(EquipmentSlot.FEET).getItem();
		final var state = this.level().getBlockState(this.blockPosition());
		if (state.is(Blocks.PLANTS)) {
			return feetItem instanceof HikingBootsItem ? 1 : original;
		}

		// Just instanceof to grab the Vanilla snow Layer block and TFC's snow Pile block
		if (state.getBlock() instanceof SnowLayerBlock) {
			final boolean hasSnowShoes;
			if (feetItem instanceof SnowShoesItem) {
				hasSnowShoes = true;
			} else if (SNSUtils.isCuriosPresent()) {
				hasSnowShoes = CuriosUtils.getEquippedCurios((LivingEntity) (Entity) this)
						.map(ItemSlot::stream)
						.orElse(Stream.empty())
						.anyMatch(ItemSlot.contentsMatch(itemStack -> itemStack.getItem() instanceof SnowShoesItem));
			} else hasSnowShoes = false;

			return hasSnowShoes ? 1 : original;
		}

		return original;
	}

	@Inject(method = "onChangedBlock", at = @At("TAIL"))
	private void onChangedBlock(final BlockPos pos, final CallbackInfo ci) {
		final var inSnowBlock = this.level().getBlockState(pos).getBlock() instanceof SnowLayerBlock;

		final var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
		if (attribute == null) return;
		attribute.removeModifier(SnowShoesItem.SNOW_SHOES_UUID);

		if (getItemBySlot(EquipmentSlot.FEET).getItem() instanceof SnowShoesItem snowShoes) {
			final var snowShoesProperties = snowShoes.getSnowShoesProperties();
			if (inSnowBlock) {
				attribute.addTransientModifier(
						new AttributeModifier(SnowShoesItem.SNOW_SHOES_UUID, SnowShoesItem.SNOW_NAME, snowShoesProperties.speedBonus(),
								Operation.MULTIPLY_TOTAL));
			} else {
				attribute.addTransientModifier(
						new AttributeModifier(SnowShoesItem.SNOW_SHOES_UUID, SnowShoesItem.NON_SNOW_NAME, snowShoesProperties.speedPenalty(),
								Operation.MULTIPLY_TOTAL));
			}
		}

		if (SNSUtils.isCuriosPresent()) {
			CuriosUtils.getEquippedCurios((LivingEntity) (Entity) this)
					.map(ItemSlot::stream)
					.orElse(Stream.empty())
					.filter(ItemSlot.contentsMatch(itemStack -> itemStack.getItem() instanceof SnowShoesItem))
					.findAny()
					.ifPresent(slot -> {
						final var snowShoesProperties = ((SnowShoesItem) slot.getStack().getItem()).getSnowShoesProperties();
						if (inSnowBlock) {
							attribute.addTransientModifier(
									new AttributeModifier(SnowShoesItem.SNOW_SHOES_UUID, SnowShoesItem.SNOW_NAME, snowShoesProperties.speedBonus(),
											Operation.MULTIPLY_TOTAL));
						} else {
							attribute.addTransientModifier(new AttributeModifier(SnowShoesItem.SNOW_SHOES_UUID, SnowShoesItem.NON_SNOW_NAME,
									snowShoesProperties.speedPenalty(), Operation.MULTIPLY_TOTAL));
						}
					});
		}
	}
}