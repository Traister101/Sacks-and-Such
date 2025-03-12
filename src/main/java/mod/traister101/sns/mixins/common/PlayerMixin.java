package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.traister101.sns.common.items.HikingBootsItem;
import net.dries007.tfc.common.TFCTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	/**
	 * @reason When worn boots should prevent TFC plants from slowing down players
	 * @author Traister101
	 */
	@ModifyReturnValue(method = "getBlockSpeedFactor", at = @At(value = "RETURN"))
	private float preventPlantSlowdown(final float original) {
		if (1 > original) return original;

		if (!(getItemBySlot(EquipmentSlot.FEET).getItem() instanceof HikingBootsItem)) return original;

		return this.level().getBlockState(this.blockPosition()).is(TFCTags.Blocks.PLANTS) ? 1 : original;
	}
}