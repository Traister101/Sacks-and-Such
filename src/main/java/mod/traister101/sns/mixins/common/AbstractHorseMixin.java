package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mod.traister101.sns.common.items.HorseshoesItem;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {

	@Shadow
	protected SimpleContainer inventory;
	/**
	 * For tracking the last horseshoes we had so we can remove the attributes
	 */
	@Unique
	private HorseshoesItem sns$lastHorseshoes = null;

	protected AbstractHorseMixin(final EntityType<? extends Animal> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	/**
	 * @reason We want to damage our horseshoes when they are used
	 * @author Traister101
	 */
	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void tickHorseshoe(final CallbackInfo ci) {
		final ItemStack itemStack = inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self()));
		if (itemStack.getItem() instanceof HorseshoesItem) HorseshoesItem.horseshoeTick(itemStack, level(), sns$self());
	}

	/**
	 * @reason We want our horseshoes to update the horse's attributes
	 * @author Traister101
	 */
	@Inject(method = "updateContainerEquipment", at = @At(value = "TAIL"))
	private void updateHorseshoes(final CallbackInfo ci) {
		if (this.level().isClientSide) return;
		final var currentHorseshoesStack = inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self()));

		if (sns$lastHorseshoes != null) {
			getAttributes().removeAttributeModifiers(sns$lastHorseshoes.getAttributeModifiers());
			sns$lastHorseshoes = null;
		}

		if (!(currentHorseshoesStack.getItem() instanceof final HorseshoesItem currentHorseshoes)) return;
		sns$lastHorseshoes = currentHorseshoes;
		sns$setHorseshoes(currentHorseshoesStack);
		getAttributes().addTransientAttributeModifiers(currentHorseshoes.getAttributeModifiers());
	}

	/**
	 * @reason Mixins are lame and I need to store what the container has before it updates
	 * @author Traister101
	 */
	@Inject(method = "containerChanged", at = @At(value = "HEAD"))
	private void beforeContainerChanged(final Container pInvBasic, final CallbackInfo ci,
			@Share("beforeStack") final LocalRef<ItemStack> beforeStack) {
		beforeStack.set(sns$getHorseshoes());
	}

	/**
	 * @reason Play an equip sound when horseshoes are put on
	 * @author Traister101
	 */
	@Inject(method = "containerChanged", at = @At(value = "RETURN"))
	private void horseshoesEquipped(final Container pInvBasic, final CallbackInfo ci, @Share("beforeStack") final LocalRef<ItemStack> beforeStack) {
		final var afterStack = sns$getHorseshoes();
		if (this.tickCount > 20 && afterStack.getItem() instanceof HorseshoesItem && beforeStack.get() != afterStack) {
			this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
		}
	}

	/**
	 * @reason We need to increase the inventory size by 1 to allow for our horseshoe slot
	 * @author Traister101
	 */
	@ModifyReturnValue(method = "getInventorySize", at = @At(value = "RETURN"))
	private int addHorseshoeSlot(final int original) {
		return original + 1;
	}

	/**
	 * @reason We need to save our horseshoes
	 * @author Traister101
	 */
	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	private void saveHorseshoe(final CompoundTag compoundTag, CallbackInfo ci) {
		final ItemStack item = inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self()));
		if (item.isEmpty()) return;
		compoundTag.put("HorseshoesItem", item.save(new CompoundTag()));
	}

	/**
	 * @reason We need to load our horseshoes
	 * @author Traister101
	 */
	@Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;updateContainerEquipment()V"))
	private void loadHorseshoe(final CompoundTag compoundTag, final CallbackInfo ci) {
		if (!compoundTag.contains("HorseshoesItem", CompoundTag.TAG_COMPOUND)) return;

		final ItemStack itemStack = ItemStack.of(compoundTag.getCompound("HorseshoesItem"));
		if (itemStack.is(holder -> holder.value() instanceof HorseshoesItem))
			inventory.setItem(HorseshoesItem.getHorseshoesSlot(sns$self()), itemStack);
	}

	@Unique
	private AbstractHorse sns$self() {
		return (AbstractHorse) (Animal) this;
	}

	@Unique
	private void sns$setHorseshoes(final ItemStack itemStack) {
		this.setItemSlot(EquipmentSlot.FEET, itemStack);
		this.setDropChance(EquipmentSlot.FEET, 0);
	}

	@Unique
	private ItemStack sns$getHorseshoes() {
		return getItemBySlot(EquipmentSlot.FEET);
	}
}