package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.*;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.items.HorseshoesItem;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@SuppressWarnings("AddedMixinMembersNamePattern")
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
	private void tickHorseshoe(final CallbackInfo ci) {
		final ItemStack itemStack = inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self()));
		if (itemStack.getItem() instanceof HorseshoesItem horseshoes) horseshoes.horseshoeTick(itemStack, level(), sns$self());
	}

	/**
	 * @reason We want our horseshoes to update the horse's attributes
	 * @author Traister101
	 */
	@Inject(method = "updateContainerEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;setFlag(IZ)V", shift = Shift.AFTER))
	private void updateHorseshoes(final CallbackInfo ci) {
		setHorseshoeEquipment(inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self())));
	}

	/**
	 * @reason Mixins are lame and I need to store what the container has before it updates
	 * @author Traister101
	 */
	@Inject(method = "containerChanged", at = @At(value = "HEAD"))
	private void beforeContainerChanged(final Container pInvBasic, final CallbackInfo ci,
			@Share("beforeStack") final LocalRef<ItemStack> beforeStack) {
		beforeStack.set(getHorseshoes());
	}

	/**
	 * @reason Play an equip sound when horseshoes are put on
	 * @author Traister101
	 */
	@Definition(id = "flag", local = @Local(type = boolean.class))
	@Expression("flag")
	@Inject(method = "containerChanged", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
	private void horseshoesEquipped(final Container pInvBasic, final CallbackInfo ci, @Share("beforeStack") final LocalRef<ItemStack> beforeStack) {
		final var afterStack = getHorseshoes();
		if (afterStack.getItem() instanceof HorseshoesItem && beforeStack.get() != afterStack) {
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
	 * @reason The {@link net.minecraftforge.event.entity.living.LivingFallEvent} doesn't fire for horses...
	 * @author Traister101
	 */
	@ModifyVariable(method = "calculateFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float modifyFallDistance(final float fallDistance) {
		final var attribute = getAttribute(SNSAttributes.EXTRA_FALL_DISTANCE.get());
		if (attribute == null) return fallDistance;
		return (float) Math.max(0, fallDistance - attribute.getValue());
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
	private ItemStack getHorseshoes() {
		return getItemBySlot(EquipmentSlot.FEET);
	}

	@Unique
	private void setHorseshoes(final ItemStack itemStack) {
		this.setItemSlot(EquipmentSlot.FEET, itemStack);
		this.setDropChance(EquipmentSlot.FEET, 0);
	}

	@Unique
	private void setHorseshoeEquipment(final ItemStack itemStack) {
		final var lastHorseshoes = getHorseshoes();
		setHorseshoes(itemStack);
		if (!level().isClientSide) {
			if (lastHorseshoes.getItem() instanceof HorseshoesItem horseshoes) {
				getAttributes().removeAttributeModifiers(horseshoes.getAttributeModifiers());
			}

			if (!(itemStack.getItem() instanceof final HorseshoesItem currentHorseshoes)) return;
			getAttributes().addTransientAttributeModifiers(currentHorseshoes.getAttributeModifiers());
		}
	}
}