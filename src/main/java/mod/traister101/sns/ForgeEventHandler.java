package mod.traister101.sns;

import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.handlers.PickupHandler;
import mod.traister101.sns.util.items.ItemHandlerSlot;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.minecraft.world.item.*;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
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

		if (SNSUtils.isCuriosPresent()) {
			final var maybeProjectileSlot = CuriosApi.getCuriosInventory(event.getEntity())
					.map(ICuriosItemHandler::getEquippedCurios)
					.flatMap(itemHandler -> SNSUtils.itemHandlerSlotStream(itemHandler)
							.filter(ItemHandlerSlot.contains(SNSItems.QUIVER.get()))
							.map(slot -> slot.getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve())
							.flatMap(Optional::stream)
							.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
							.flatMap(Optional::stream)
							.findFirst());

			if (maybeProjectileSlot.isPresent()) {
				event.setProjectileItemStack(maybeProjectileSlot.get().getStack().copy());
				return;
			}
		}

		final var maybeProjectileSlot = event.getEntity()
				.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.resolve()
				.flatMap(itemHandler -> SNSUtils.itemHandlerSlotStream(itemHandler)
						.filter(ItemHandlerSlot.contains(SNSItems.QUIVER.get()))
						.map(slot -> slot.getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve())
						.flatMap(Optional::stream)
						.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
						.flatMap(Optional::stream)
						.findFirst());

		maybeProjectileSlot.ifPresent(slot -> event.setProjectileItemStack(slot.getStack().copy()));
	}

	@SubscribeEvent
	public static void onProjectileLose(final ArrowLooseEvent event) {
		if (!event.hasAmmo()) return;
		if (event.getCharge() < 1) return;

		final Predicate<ItemStack> supportedProjectile;
		if (!(event.getBow().getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return;
		supportedProjectile = projectileWeaponItem.getAllSupportedProjectiles();

		if (SNSUtils.isCuriosPresent()) {
			final var maybeProjectileSlot = CuriosApi.getCuriosInventory(event.getEntity())
					.map(ICuriosItemHandler::getEquippedCurios)
					.flatMap(itemHandler -> SNSUtils.itemHandlerSlotStream(itemHandler)
							.filter(ItemHandlerSlot.contains(SNSItems.QUIVER.get()))
							.map(slot -> slot.getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve())
							.flatMap(Optional::stream)
							.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
							.flatMap(Optional::stream)
							.findFirst());

			maybeProjectileSlot.ifPresent(slot -> slot.extractItem(1, false));
		}

		final var maybeProjectileSlot = event.getEntity()
				.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.resolve()
				.flatMap(itemHandler -> SNSUtils.itemHandlerSlotStream(itemHandler)
						.filter(ItemHandlerSlot.contains(SNSItems.QUIVER.get()))
						.map(slot1 -> slot1.getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve())
						.flatMap(Optional::stream)
						.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
						.flatMap(Optional::stream)
						.findFirst());

		maybeProjectileSlot.ifPresent(slot -> slot.extractItem(1, false));
	}

	@SubscribeEvent
	public static void onEntityFall(final LivingFallEvent event) {
		final var attribute = event.getEntity().getAttribute(SNSAttributes.EXTRA_FALL_DISTANCE.get());
		if (attribute == null) return;
		event.setDistance((float) Math.max(0, event.getDistance() - attribute.getValue()));
	}
}