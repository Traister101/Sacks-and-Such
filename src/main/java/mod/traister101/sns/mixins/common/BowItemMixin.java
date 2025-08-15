package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.items.ItemSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

import java.util.Optional;

@Mixin(BowItem.class)
public class BowItemMixin {

	/**
	 * @reason The Forge {@link LivingGetProjectileEvent} doesn't work for us because it's fired twice in the
	 * process of drawing back the bow. First it's fired in {@link BowItem#use(Level, Player, InteractionHand)} (will not modify the stack) and then
	 * second it's fired in {@link BowItem#releaseUsing(ItemStack, Level, LivingEntity, int)} (will modify the stack) with no reasonable way to switch
	 * out the stack outside of handling the whole arrow shooting process via {@link ArrowLooseEvent}.
	 * @author Traister101
	 */
	@Definition(id = "getProjectile", method = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
	@Expression("? = ?.getProjectile(?)")
	@ModifyVariable(method = "releaseUsing", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = Shift.AFTER), ordinal = 1)
	private ItemStack getProjectileFromQuiver(final ItemStack originalProjectile, @Local(argsOnly = true) ItemStack bow, @Local Player player) {
		// Already found a projectile
		if (!originalProjectile.isEmpty()) return originalProjectile;

		if (!(bow.getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return originalProjectile;

		final var supportedProjectile = projectileWeaponItem.getAllSupportedProjectiles();

		final var maybeProjectileSlot = SNSUtils.curiosAndInventoryStream(player)
				.flatMap(ItemSlot::stream)
				.filter(ItemSlot.contains(SNSItems.QUIVER.get()))
				.map(ItemSlot.extractCapability(ForgeCapabilities.ITEM_HANDLER))
				.map(LazyOptional::resolve)
				.flatMap(Optional::stream)
				.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
				.flatMap(Optional::stream)
				.findFirst();

		return maybeProjectileSlot.map(slot -> slot.extractItem(1, false)).orElse(originalProjectile);
	}
}