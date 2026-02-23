package mod.traister101.sns.mixins.common.feature.quiver;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.items.ItemSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

	/**
	 * @reason Crossbows are even worse than bows and require mixins to work properly with a quiver type item. Very cool
	 */
	@Definition(id = "getProjectile", method = "Lnet/minecraft/world/entity/LivingEntity;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
	@Expression("?.getProjectile(?)")
	@ModifyExpressionValue(method = "tryLoadProjectiles", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ItemStack extractProjectileFromQuiver(final ItemStack originalProjectile, final LivingEntity shooter,
			final ItemStack crossbowStack) {
		// Already found a projectile
		if (!originalProjectile.isEmpty()) return originalProjectile;

		if (!(crossbowStack.getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return originalProjectile;

		return SNSUtils.extractProjectileFromQuiver(shooter, projectileWeaponItem.getAllSupportedProjectiles()).orElse(originalProjectile);
	}

	/**
	 * @reason Vanilla calls {@link LivingEntity#getProjectile(ItemStack)} which like the bow case isn't satisfactory
	 */
	@Definition(id = "getProjectile", method = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
	@Definition(id = "isEmpty", method = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
	@Expression("?.getProjectile(?).isEmpty()")
	@ModifyExpressionValue(method = "use", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean hasProjectileInQuiver(final boolean lacksProjectile, @Local(argsOnly = true) final Player player,
			@SuppressWarnings("LocalMayUseName") @Local final ItemStack crossbowStack) {
		if (!lacksProjectile) return false;

		if (!(crossbowStack.getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return false;

		return SNSUtils.findFirstSlotInQuiver(player, projectileWeaponItem.getAllSupportedProjectiles())
				.map(ItemSlot::getStack)
				.map(ItemStack::isEmpty)
				.isEmpty();
	}
}