package mod.traister101.sacks.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public final class SNSUtils {
	
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
	
    @Nonnull
	public static <T> T getNull() {
		return null;
	}
    
    public static IItemHandler getHandler(@Nonnull ItemStack stack) {
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}
}