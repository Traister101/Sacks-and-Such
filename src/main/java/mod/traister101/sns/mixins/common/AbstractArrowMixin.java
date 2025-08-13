package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.handlers.PickupHandler;
import mod.traister101.sns.util.items.ItemHandlerSlot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.items.ItemHandlerHelper;

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
	@ModifyReturnValue(method = "tryPickup", at = @At(value = "RETURN", ordinal = 0))
	private boolean tryInsertIntoQuiver(final boolean fitInsideInventory, final Player player) {
		if (fitInsideInventory) return true;

		for (final var handler : SNSUtils.curiosAndInventory(player)) {
			for (final var quiverSlot : ItemHandlerSlot.iterable(handler)) {
				final var quiverStack = quiverSlot.getStack();
				if (!quiverStack.is(SNSItems.QUIVER.get())) continue;

				final var maybeItemHandler = quiverStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
				if (maybeItemHandler.isEmpty()) continue;

				final var itemHandler = maybeItemHandler.get();
				final ItemStack remainder = ItemHandlerHelper.insertItemStacked(itemHandler, this.getPickupItem(), false);
				if (remainder.isEmpty()) return true;
			}
		}

		return false;
	}
}