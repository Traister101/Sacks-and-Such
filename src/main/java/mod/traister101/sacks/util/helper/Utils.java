package mod.traister101.sacks.util.helper;

import javax.annotation.Nonnull;

import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class Utils {
	
	public static boolean isSealed(ItemStack stack) {
		return stack.getItem() instanceof ItemThrowableVessel && stack.hasTagCompound() && stack.getTagCompound().getBoolean(ToggleType.SEAL.toString());
	}
	
	public static boolean isAutoVoid(ItemStack stack) {
		return stack.getItem() instanceof ItemSack && stack.hasTagCompound() && stack.getTagCompound().getBoolean(ToggleType.VOID.toString());
	}
	
	public static boolean isAutoPickup(ItemStack stack) {
		return stack.getItem() instanceof ItemSack && stack.hasTagCompound() && stack.getTagCompound().getBoolean(ToggleType.PICKUP.toString());
	}
	
	public static void toggle(ItemStack stack, ToggleType type, boolean toggle) {
		NBTTagCompound tag = getTagSafely(stack);
		tag.setBoolean(type.toString(), toggle);
		stack.setTagCompound(tag);
	}
	
	public static NBTTagCompound getTagSafely(ItemStack stack) {
		return stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
	}
	
	public enum ToggleType {
		SEAL,
		VOID,
		PICKUP,
		NULL;
		
		private static final ToggleType[] values = values();
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
		
		
		@Nonnull
		public static ToggleType valueOf(int id) {
			return id < 0 || id >= values.length ? NULL : values[id];
		}
	}
}