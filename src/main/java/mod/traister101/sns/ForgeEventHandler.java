package mod.traister101.sns;

import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.handlers.PickupHandler;
import mod.traister101.sns.util.items.*;

import net.minecraft.world.item.*;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.*;

import java.util.Optional;
import java.util.function.Predicate;

public final class ForgeEventHandler {

	public static void init() {
		final var eventBus = MinecraftForge.EVENT_BUS;
		eventBus.register(ForgeEventHandler.class);

		eventBus.addListener(PickupHandler::onPickupItem);
		// We want to handle this last to ensure we don't trample on anybody else
		eventBus.addListener(EventPriority.LOWEST, PickupHandler::onGroundBlockInteract);
	}

	@SubscribeEvent
	public static void onProjectilePrepare(final LivingGetProjectileEvent event) {
		if (!(event.getProjectileWeaponItemStack().getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return;

		final var supportedProjectile = projectileWeaponItem.getAllSupportedProjectiles();

		final ItemStack projectileItemStack = event.getProjectileItemStack();
		if (!projectileItemStack.isEmpty()) return;

		final var maybeProjectileSlot = SNSUtils.curiosAndInventoryStream(event.getEntity())
				.flatMap(ItemSlot::stream)
				.filter(ItemSlot.contains(SNSItems.QUIVER.get()))
				.map(ItemSlot.extractCapability(ForgeCapabilities.ITEM_HANDLER))
				.map(LazyOptional::resolve)
				.flatMap(Optional::stream)
				.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
				.flatMap(Optional::stream)
				.findFirst();

		maybeProjectileSlot.ifPresent(slot -> event.setProjectileItemStack(slot.getStack().copy()));
	}

	@SubscribeEvent
	public static void onProjectileLose(final ArrowLooseEvent event) {
		if (!event.hasAmmo()) return;
		if (event.getCharge() < 1) return;

		final Predicate<ItemStack> supportedProjectile;
		if (!(event.getBow().getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return;
		supportedProjectile = projectileWeaponItem.getAllSupportedProjectiles();

		final var maybeProjectileSlot = SNSUtils.curiosAndInventoryStream(event.getEntity())
				.flatMap(ItemSlot::stream)
				.filter(ItemSlot.contains(SNSItems.QUIVER.get()))
				.map(ItemSlot.extractCapability(ForgeCapabilities.ITEM_HANDLER))
				.map(LazyOptional::resolve)
				.flatMap(Optional::stream)
				.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
				.flatMap(Optional::stream)
				.findFirst();

		maybeProjectileSlot.ifPresent(slot -> slot.extractItem(1, false));
	}

	@SubscribeEvent
	public static void onEntityFall(final LivingFallEvent event) {
		final var attribute = event.getEntity().getAttribute(SNSAttributes.EXTRA_FALL_DISTANCE.get());
		if (attribute == null) return;
		event.setDistance((float) Math.max(0, event.getDistance() - attribute.getValue()));
	}
}