package mod.traister101.sns.mixins.common;

import mod.traister101.sns.common.items.HikingBootsItem;
import net.dries007.tfc.common.TFCTags;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Shadow
	public abstract ItemStack getItemBySlot(final EquipmentSlot pSlot);

	/**
	 * @reason When worn boots should prevent TFC plants from slowing down players
	 * @author Traister101
	 */
	@Inject(method = "getBlockSpeedFactor", at = @At(value = "RETURN"), cancellable = true)
	private void sns$preventPlantSlowdownWhenWearingBoots(CallbackInfoReturnable<Float> callbackInfo) {
		if (1 > callbackInfo.getReturnValueF()) return;

		final ItemStack boots = getItemBySlot(EquipmentSlot.FEET);
		if (!(boots.getItem() instanceof HikingBootsItem)) return;

		final BlockState blockState = this.level().getBlockState(this.blockPosition());
		if (blockState.is(TFCTags.Blocks.PLANTS)) callbackInfo.setReturnValue(1F);
	}
}