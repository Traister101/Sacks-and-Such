package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.esc.network.utils.ByteBufUtils;
import net.dries007.tfc.common.capabilities.ItemStackCapabilitySync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Copy TFCs vanilla FriendlyByteBuf mixin for the Extended Slot Capacity helpers
 *
 * @see ItemStackCapabilitySync
 */
@Mixin(value = ByteBufUtils.class, remap = false)
public abstract class ByteBufUtilsMixin {

	/**
	 * @reason TFC injects into {@link net.minecraft.network.FriendlyByteBuf#writeItemStack(ItemStack, boolean)} and redirects the NBT tag write
	 * to {@link ItemStackCapabilitySync#writeToNetwork(ItemStack, CompoundTag)}. Extended Slot Capacity has custom networking logic to support
	 * extended slot capacity, and we need to copy over the mixin.
	 * This <i>should</i> be a {@link Redirect} but it doesn't work in prod for some reason and I can't figure it out. This however does. :|
	 * @author Traister101
	 */
	@ModifyVariable(method = "writeExtendedItemStack", at = @At(value = "LOAD"), ordinal = 0)
	private static CompoundTag writeTFCSyncableCapabilityData(final CompoundTag value, @Local(argsOnly = true) final ItemStack itemStack) {
		return ItemStackCapabilitySync.writeToNetwork(itemStack, value);
	}

	@Redirect(method = "readExtendedItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;readShareTag(Lnet/minecraft/nbt/CompoundTag;)V"))
	private static void readTFCSyncableCapabilityData(final ItemStack itemStack, final CompoundTag compoundTag) {
		ItemStackCapabilitySync.readFromNetwork(itemStack, compoundTag);
	}
}