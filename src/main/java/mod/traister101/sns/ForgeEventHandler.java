package mod.traister101.sns;

import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.handlers.PickupHandler;
import mod.traister101.sns.util.items.ItemSlot;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ProjectileWeaponItem;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.*;

import java.util.Optional;

public final class ForgeEventHandler {

	public static void init(final IEventBus eventBus) {
		eventBus.register(ForgeEventHandler.class);

		eventBus.addListener(PickupHandler::onPickupItem);
		// We want to handle this last to ensure we don't trample on anybody else
		eventBus.addListener(EventPriority.LOWEST, PickupHandler::onGroundBlockInteract);
	}

	@SubscribeEvent
	public static void onProjectilePrepare(final ArrowNockEvent event) {
		if (!(event.getBow().getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return;

		final var supportedProjectile = projectileWeaponItem.getAllSupportedProjectiles();

		final var maybeProjectileSlot = SNSUtils.curiosAndInventoryStream(event.getEntity())
				.flatMap(ItemSlot::stream)
				.filter(ItemSlot.contains(SNSItems.QUIVER.get()))
				.map(ItemSlot.extractCapability(ForgeCapabilities.ITEM_HANDLER))
				.map(LazyOptional::resolve)
				.flatMap(Optional::stream)
				.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
				.flatMap(Optional::stream)
				.findFirst();

		maybeProjectileSlot.ifPresent(slot -> {
			event.setAction(InteractionResultHolder.consume(event.getBow()));
			event.getEntity().startUsingItem(event.getHand());
		});
	}

	@SubscribeEvent
	public static void onEntityFall(final LivingFallEvent event) {
		final var attribute = event.getEntity().getAttribute(SNSAttributes.EXTRA_FALL_DISTANCE.get());
		if (attribute == null) return;
		event.setDistance((float) Math.max(0, event.getDistance() - attribute.getValue()));
	}
}