package mod.traister101.sns.mixins.common;

import mod.traister101.sns.common.items.SNSItems;
import net.dries007.tfc.common.items.JavelinItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Optional;

@Mixin(value = JavelinItem.class)
public abstract class JavelinItemMixin extends SwordItem {

	public JavelinItemMixin(final Tier pTier, final int pAttackDamageModifier, final float pAttackSpeedModifier, final Properties pProperties) {
		super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
	}

	/**
	 * @reason TFCs {@link JavelinItem} doesn't fire an event or anything helpful so to replace the thrown Javelin we must use mixin.
	 * @author Traister101
	 */
	@Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getInventory()Lnet/minecraft/world/entity/player/Inventory;"), cancellable = true)
	private void sns$replaceThrownJavelin(final ItemStack stack, final Level level, final LivingEntity entity, final int ticksLeft,
			final CallbackInfo ci) {
		entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().flatMap(entityInventory -> {
			for (int entitySlot = 0; entitySlot < entityInventory.getSlots(); entitySlot++) {
				final ItemStack stackInSlot = entityInventory.getStackInSlot(entitySlot);
				if (!stackInSlot.is(SNSItems.QUIVER.get())) continue;

				return stackInSlot.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().map(itemHandler -> {
					for (int slotIndex = 0; slotIndex < itemHandler.getSlots(); slotIndex++) {
						final ItemStack ammoStack = itemHandler.getStackInSlot(slotIndex);
						if (ammoStack.getItem() instanceof JavelinItem) return itemHandler.extractItem(slotIndex, 1, false);
					}
					return ItemStack.EMPTY;
				});
			}
			return Optional.empty();
		}).ifPresent(itemStack -> {
			final Player player = (Player) entity;
			final Inventory inventory = player.getInventory();
			switch (entity.getUsedItemHand()) {
				case MAIN_HAND -> inventory.setItem(inventory.selected, itemStack);
				case OFF_HAND -> inventory.setItem(Inventory.SLOT_OFFHAND, itemStack);
			}
			player.awardStat(Stats.ITEM_USED.get(this));
			ci.cancel();
		});
	}
}