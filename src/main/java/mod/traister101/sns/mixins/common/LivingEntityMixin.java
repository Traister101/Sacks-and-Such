package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.traister101.sns.common.items.*;
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

	@Shadow
	public abstract ItemStack getOffhandItem();

	@Shadow
	public abstract ItemStack getMainHandItem();

	/**
	 * @reason In order for our slowdown preventing items to work we need to modify the speed factor
	 * @author Traister101
	 */
	@ModifyReturnValue(method = "getBlockSpeedFactor", at = @At("RETURN"))
	private float modifyBlockSpeedFactor(final float original) {
		if (original >= 1) return original;

		final var feetItem = getItemBySlot(EquipmentSlot.FEET).getItem();
		final var state = this.level().getBlockState(this.blockPosition());
		if (state.is(Blocks.PLANTS)) {
			if (feetItem instanceof HikingBootsItem) return 1;
			if (getMainHandItem().getItem() instanceof WalkingStickItem || getOffhandItem().getItem() instanceof WalkingStickItem) return 1;
		}

		return original;
	}
}