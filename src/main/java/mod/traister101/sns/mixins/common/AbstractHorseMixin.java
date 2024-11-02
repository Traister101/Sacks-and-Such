package mod.traister101.sns.mixins.common;

import mod.traister101.sns.common.items.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {

	@Shadow
	protected SimpleContainer inventory;

	protected AbstractHorseMixin(final EntityType<? extends Animal> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	/**
	 * @reason We want to damage our horseshoes when they are used
	 * @author Traister101
	 */
	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void sns$tickHorseshoe(final CallbackInfo ci) {
		final ItemStack itemStack = inventory.getItem(HorseshoesItem.getHorseshoesSlot(getSelf()));
		if (itemStack.getItem() instanceof HorseshoesItem) HorseshoesItem.horseshoeTick(itemStack, level(), getSelf());
	}

	@Unique
	@SuppressWarnings("AddedMixinMembersNamePattern")
	private AbstractHorse getSelf() {
		return (AbstractHorse) (Animal) this;
	}

	/**
	 * @reason We want our horseshoes to
	 * @author Traister101
	 */
	@Inject(method = "updateContainerEquipment", at = @At(value = "TAIL"))
	private void sns$updateHorseshoe(final CallbackInfo ci) {
		if (this.level().isClientSide) return;
		//noinspection DataFlowIssue
		getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(HorseshoesItem.HORSE_SHOE_UUID);
		if (!(inventory.getItem(HorseshoesItem.getHorseshoesSlot(getSelf())).getItem() instanceof final HorseshoesItem horseshoes)) return;

		final double speedModifier = horseshoes.horseshoeSpeedModifier.get();
		if (speedModifier <= 0) return;
		//noinspection DataFlowIssue
		getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(
				new AttributeModifier(HorseshoesItem.HORSE_SHOE_UUID, "Horseshoe movement speed bonus", speedModifier, Operation.MULTIPLY_TOTAL));
	}

	/**
	 * @reason We need to increase the inventory size by 1 to allow for our horseshoe slot
	 * @author Traister101
	 */
	@Inject(method = "getInventorySize", at = @At(value = "RETURN"), cancellable = true)
	private void sns$addHorseshoeSlot(final CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(cir.getReturnValueI() + 1);
	}

	/**
	 * @reason We need to save our horseshoes
	 * @author Traister101
	 */
	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	private void sns$saveHorseshoe(final CompoundTag compoundTag, CallbackInfo ci) {
		final ItemStack item = inventory.getItem(HorseshoesItem.getHorseshoesSlot(getSelf()));
		if (item.isEmpty()) return;
		compoundTag.put("HorseshoesItem", item.save(new CompoundTag()));
	}

	/**
	 * @reason We need to load our horseshoes
	 * @author Traister101
	 */
	@Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;updateContainerEquipment()V"))
	private void sns$loadHorseshoe(final CompoundTag compoundTag, final CallbackInfo ci) {
		if (!compoundTag.contains("HorseshoesItem", CompoundTag.TAG_COMPOUND)) return;

		final ItemStack itemStack = ItemStack.of(compoundTag.getCompound("HorseshoesItem"));
		if (itemStack.is(SNSItems.STEEL_HORSESHOES.get())) inventory.setItem(HorseshoesItem.getHorseshoesSlot(getSelf()), itemStack);
	}
}