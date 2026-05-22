package mod.traister101.sns.util;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.util.SNSUtils.ToggleType;

import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class NBTHelper {

	public static void toggle(final ItemStack heldStack, final ToggleType toggleType, final boolean toggle) {
		heldStack.getOrCreateTag().putBoolean(toggleType.tag, toggle);
	}

	public static boolean isAutoPickup(final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ContainerItem)) {
			return false;
		}

		final CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag == null) return false;

		if (compoundTag.contains(ToggleType.PICKUP.tag, Tag.TAG_BYTE)) return compoundTag.getBoolean(ToggleType.PICKUP.tag);

		return true;
	}
}