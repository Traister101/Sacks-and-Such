package mod.traister101.sns;

import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.handlers.PickupHandler;

import net.minecraft.world.item.*;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.*;

import lombok.experimental.UtilityClass;
import java.util.Optional;

@UtilityClass
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

		event.getEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().flatMap(entityInventory -> {
			for (int entitySlot = 0; entitySlot < entityInventory.getSlots(); entitySlot++) {
				final ItemStack stackInSlot = entityInventory.getStackInSlot(entitySlot);
				if (!stackInSlot.is(SNSItems.QUIVER.get())) continue;

				return stackInSlot.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().map(itemHandler -> {
					for (int slotIndex = 0; slotIndex < itemHandler.getSlots(); slotIndex++) {
						final ItemStack ammoStack = itemHandler.getStackInSlot(slotIndex);
						if (supportedProjectile.test(ammoStack)) return ammoStack;
					}
					return ItemStack.EMPTY;
				});
			}
			return Optional.empty();
		}).ifPresent(event::setProjectileItemStack);
	}

	@SubscribeEvent
	public static void onEntityFall(final LivingFallEvent event) {
		final var attribute = event.getEntity().getAttribute(SNSAttributes.EXTRA_FALL_DISTANCE.get());
		if (attribute == null) return;
		event.setDistance((float) Math.max(0, event.getDistance() - attribute.getValue()));
	}
}