package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.traister101.sns.common.items.HorseshoesItem;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin extends AbstractContainerMenu {

	protected HorseInventoryMenuMixin(@Nullable final MenuType<?> pMenuType, final int pContainerId) {
		super(pMenuType, pContainerId);
	}

	/**
	 * @reason We have to add another slot to the horse inventory menu for our horseshoe
	 * @author Traister101
	 */
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void addHorseshoesSlot(final int id, final Inventory inventory, final Container container, final AbstractHorse horse,
			final CallbackInfo ci) {
		final Slot horseshoeSlot = this.addSlot(new Slot(container, HorseshoesItem.getHorseshoesSlot(horse), 8, (horse.canWearArmor() ? 54 : 36)) {

			@Override
			public boolean mayPlace(final ItemStack itemStack) {
				return itemStack.getItem() instanceof HorseshoesItem && !this.hasItem();
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});

		final int size = slots.size();
		Slot slot = horseshoeSlot;
		// Squeeze the horseshoe slot right after the armor or saddle
		for (int slotIndex = HorseshoesItem.getHorseshoesSlot(horse); slotIndex < size; slotIndex++) {
			slot.index = slotIndex;
			slot = slots.set(slotIndex, slot);
		}
	}

	/**
	 * @reason Our extra slot messes with some of the quick move constants
	 * @author Traister101
	 */
	@Definition(id = "moveItemStackTo", method = "Lnet/minecraft/world/inventory/HorseInventoryMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z")
	@Expression(value = "? <= @(2)", id = "test")
	@Expression(value = "this.moveItemStackTo(?, @(2), ?, false)", id = "moveStack")
	@ModifyExpressionValue(method = "quickMoveStack", at = {@At(value = "MIXINEXTRAS:EXPRESSION", id = "test"), @At(value = "MIXINEXTRAS:EXPRESSION", id = "moveStack")})
	private int accountForHorseshoesSlot(final int value) {
		return value + 1;
	}

	/**
	 * @reason Our horseshoes slot isn't properly prioritised like armor or saddles by vanilla
	 * @author Traister101
	 */
	@Definition(id = "getSlot", method = "Lnet/minecraft/world/inventory/HorseInventoryMenu;getSlot(I)Lnet/minecraft/world/inventory/Slot;")
	@Definition(id = "mayPlace", method = "Lnet/minecraft/world/inventory/Slot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z")
	@Expression("@(this.getSlot(1)).mayPlace(?)")
	@Inject(method = "quickMoveStack", at = @At(value = "MIXINEXTRAS:EXPRESSION"), cancellable = true)
	private void quickMoveHorseshoes(final Player player, final int slotIndex, final CallbackInfoReturnable<ItemStack> cir) {
		final var slotStack = this.getSlot(slotIndex).getItem();
		if (getSlot(2).mayPlace(slotStack) && !getSlot(2).hasItem()) {
			if (!moveItemStackTo(slotStack, 2, 3, false)) {
				cir.setReturnValue(ItemStack.EMPTY);
			}
		}
	}
}