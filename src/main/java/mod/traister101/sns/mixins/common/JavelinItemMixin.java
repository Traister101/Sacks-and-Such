package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.items.*;
import net.dries007.tfc.common.items.JavelinItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

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
	@Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getInventory()Lnet/minecraft/world/entity/player/Inventory;"))
	private void replaceThrownJavelin(final ItemStack stack, final Level level, final LivingEntity entity, final int ticksLeft, final CallbackInfo ci,
			@Local final Player player) {
		final var replaceJavalinSlot = SNSUtils.curiosAndInventoryStream(entity)
				.flatMap(ItemSlot::stream)
				.filter(ItemSlot.contains(SNSItems.QUIVER.get()))
				.map(ItemSlot.extractCapability(ForgeCapabilities.ITEM_HANDLER))
				.map(LazyOptional::resolve)
				.flatMap(Optional::stream)
				.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, itemStack -> itemStack.is(SNSItemTags.TFC_JAVELINS)))
				.flatMap(Optional::stream)
				.findFirst();

		if (replaceJavalinSlot.isPresent()) {
			final var slot = replaceJavalinSlot.get();
			final Inventory inventory = player.getInventory();
			final var replacementJavalin = slot.extractItem(slot.getStack().getMaxStackSize(), false);
			switch (entity.getUsedItemHand()) {
				case MAIN_HAND -> inventory.setItem(inventory.selected, replacementJavalin);
				case OFF_HAND -> inventory.setItem(Inventory.SLOT_OFFHAND, replacementJavalin);
			}
		}
	}
}