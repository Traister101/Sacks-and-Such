package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.handlers.PickupHandler;
import mod.traister101.sns.util.items.ItemSlot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

	protected AbstractArrowMixin(final EntityType<? extends Projectile> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Shadow
	protected abstract ItemStack getPickupItem();

	/**
	 * @reason Arrows are annoying and don't fire an event like {@link ItemEntity}s do. {@link PickupHandler#onPickupItem(EntityItemPickupEvent)}
	 * handles the item entity case automatically
	 * @author Traister101
	 */
	@Definition(id = "add", method = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z")
	@Definition(id = "player", local = @Local(type = Player.class, argsOnly = true))
	@Definition(id = "getInventory", method = "Lnet/minecraft/world/entity/player/Player;getInventory()Lnet/minecraft/world/entity/player/Inventory;")
	@Definition(id = "getPickupItem", method = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPickupItem()Lnet/minecraft/world/item/ItemStack;")
	@Expression("player.getInventory().add(this.getPickupItem())")
	@Inject(method = "tryPickup", at = @At(value = "MIXINEXTRAS:EXPRESSION"), cancellable = true)
	private void tryInsertIntoQuiver(final Player player, final CallbackInfoReturnable<Boolean> cir) {
		// Merge with arrows already in the inventory first
		final var playerHandler = new PlayerMainInvWrapper(player.getInventory());
		final var pickupItem = this.getPickupItem();

		if (pickupItem.getCount() > 1) {
			SacksNSuch.LOGGER.warn("Arrow {} has a stack that's larger than 1 {}. Quiver insertion will not function as expected", this.getTypeName(),
					pickupItem);
		}

		if (pickupItem.is(SNSItemTags.TFC_JAVELINS)) {
			// There are no javelins in the player inventory
			if (ItemSlot.stream(playerHandler).noneMatch(ItemSlot.contains(SNSItemTags.TFC_JAVELINS))) {
				return;
			}
		}
		final var inventoryRemainder = SNSUtils.insertItemOnlyStacked(playerHandler, pickupItem);

		if(inventoryRemainder.getCount() < 1) {
			cir.setReturnValue(true);
			return;
		}

		for (final var handler : SNSUtils.curiosAndInventory(player)) {
			for (final var quiverSlot : ItemSlot.iterable(handler)) {
				final var quiverStack = quiverSlot.getStack();
				if (!quiverStack.is(SNSItems.QUIVER.get())) continue;

				final var maybeItemHandler = quiverStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
				if (maybeItemHandler.isEmpty()) continue;

				final ItemStack remainder = ItemHandlerHelper.insertItemStacked(maybeItemHandler.get(), inventoryRemainder, false);
				if (!remainder.isEmpty()) continue;

				cir.setReturnValue(true);
				return;
			}
		}
	}
}