package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.esc.network.utils.ByteBufUtils;
import net.dries007.tfc.common.capabilities.ItemStackCapabilitySync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

/**
 * Copy TFCs vanilla FriendlyByteBuf mixin for the Extended Slot Capacity helpers
 *
 * @see ItemStackCapabilitySync
 */
@Mixin(value = ByteBufUtils.class, remap = false)
public class ByteBufUtilsMixin {

	@Redirect(method = "writeExtendedItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeNbt(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/network/FriendlyByteBuf;"))
	private static FriendlyByteBuf writeSyncableCapabilityData(final FriendlyByteBuf instance, final CompoundTag compoundTag,
			@Local(argsOnly = true) ItemStack itemStack) {
		return instance.writeNbt(ItemStackCapabilitySync.writeToNetwork(itemStack, compoundTag));
	}

	@Redirect(method = "readExtendedItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;readShareTag(Lnet/minecraft/nbt/CompoundTag;)V"))
	private static void readSyncableCapabilityData(final ItemStack itemStack, final CompoundTag compoundTag) {
		ItemStackCapabilitySync.readFromNetwork(itemStack, compoundTag);
	}
}