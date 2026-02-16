package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.traister101.sns.common.items.HikingBootsItem;
import net.dries007.tfc.common.TFCTags.Blocks;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	protected LivingEntityMixin(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Shadow
	public abstract ItemStack getItemBySlot(final EquipmentSlot pSlot);

	/**
	 * @reason In order for our Boots to work we need to modify the speed factor accounting for our Boots
	 * @author Traister101
	 */
	@ModifyReturnValue(method = "getBlockSpeedFactor", at = @At("RETURN"))
	private float modifyBlockSpeedFactor(final float original) {
		if (original >= 1) return original;

		final var feetItem = getItemBySlot(EquipmentSlot.FEET).getItem();
		final var state = this.level().getBlockState(this.blockPosition());
		final var stateBelow = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
		if (state.is(Blocks.PLANTS) || stateBelow.is(Blocks.PLANTS)) {
			return feetItem instanceof HikingBootsItem ? 1 : original;
		}

		return original;
	}
}