package mod.traister101.sns.mixins.common.feature.quiver;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.items.*;
import net.dries007.tfc.common.items.JavelinItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;

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
	@Definition(id = "player", local = @Local(type = Player.class))
	@Definition(id = "getInventory", method = "Lnet/minecraft/world/entity/player/Player;getInventory()Lnet/minecraft/world/entity/player/Inventory;")
	@Definition(id = "removeItem", method = "Lnet/minecraft/world/entity/player/Inventory;removeItem(Lnet/minecraft/world/item/ItemStack;)V")
	@Expression("player.getInventory().removeItem(?)")
	@WrapWithCondition(method = "releaseUsing", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
	private boolean replaceThrownJavelin(final Inventory instance, final ItemStack stack, @Local final Player player) {
		final var replaceJavalinSlot = SNSUtils.findFirstSlotInQuiver(player, itemStack -> itemStack.is(SNSItemTags.TFC_JAVELINS));

		if (replaceJavalinSlot.isEmpty()) return true;

		final var slot = replaceJavalinSlot.get();
		final var inventory = player.getInventory();
		final var replacementJavalin = slot.extractItem(slot.getStack().getMaxStackSize(), false);
		switch (player.getUsedItemHand()) {
			case MAIN_HAND -> inventory.setItem(inventory.selected, replacementJavalin);
			case OFF_HAND -> inventory.setItem(Inventory.SLOT_OFFHAND, replacementJavalin);
		}
		return false;
	}
}