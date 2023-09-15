package mod.traister101.sacks.util;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public final class NBTHelper {

	public static boolean isSealed(@Nonnull ItemStack stack) {
		return stack.getItem() instanceof ItemThrowableVessel && getTagSafely(stack).getBoolean(ToggleType.SEAL.key);
	}

	public static boolean isAutoVoid(@Nonnull ItemStack stack) {
		return stack.getItem() instanceof ItemSack && getTagSafely(stack).getBoolean(ToggleType.VOID.key);
	}

	public static boolean isAutoPickup(@Nonnull ItemStack stack) {
		if (stack.getItem() instanceof ItemSack)
			if (!getTagSafely(stack).hasKey(ToggleType.PICKUP.key)) {
				SacksNSuch.getNetwork().sendToServer(new TogglePacket(true, ToggleType.PICKUP));
				return true;
			}
		return getTagSafely(stack).getBoolean(ToggleType.PICKUP.key);
	}

	public static boolean doesSackHaveItems(@Nonnull ItemStack stack) {
		return stack.getItem() instanceof ItemSack && getTagSafely(stack).getBoolean(ToggleType.ITEMS.key);
	}

	@Nonnull
	public static NBTTagCompound getTagSafely(@Nonnull ItemStack stack) {
		return stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
	}
}