package mod.traister101.sacks.util;

import javax.annotation.Nonnull;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public final class SNSUtils {
	
	public static boolean isSealed(@Nonnull ItemStack stack) {
		return stack.getItem() instanceof ItemThrowableVessel && getTagSafely(stack).getBoolean(ToggleType.SEAL.toString());
	}
	
	public static boolean isAutoVoid(@Nonnull ItemStack stack) {
		return stack.getItem() instanceof ItemSack && getTagSafely(stack).getBoolean(ToggleType.VOID.toString());
	}
	
	public static boolean isAutoPickup(@Nonnull ItemStack stack) {
		if (!(stack.getItem() instanceof ItemSack)) return false;
		
		if (stack.hasTagCompound()) {
			return getTagSafely(stack).getBoolean(ToggleType.PICKUP.toString());
		} else {
			toggle(stack, ToggleType.PICKUP, true);
			return getTagSafely(stack).getBoolean(ToggleType.PICKUP.toString());
		}
		
	}
	
	public static void toggle(@Nonnull ItemStack stack, @Nonnull ToggleType type, boolean toggle) {
		NBTTagCompound tag = getTagSafely(stack);
		tag.setBoolean(type.toString(), toggle);
		stack.setTagCompound(tag);
	}
	
	@Nonnull
	public static NBTTagCompound getTagSafely(@Nonnull ItemStack stack) {
		return stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
	}
	
    @Nonnull
	public static <T> T getNull() {
		return null;
	}
    
    public static IItemHandler getHandler(@Nonnull ItemStack stack) {
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}
    
    public static void sendPacketAndStatus(boolean bool, @Nonnull ToggleType type) {
    	SacksNSuch.getNetwork().sendToServer(new TogglePacket(!bool, type));
		TextComponentTranslation statusMessage = new TextComponentTranslation(SacksNSuch.MODID + ".sack." + type.getLang() + "." + (bool ? "disabled" : "enabled"));
		Minecraft.getMinecraft().player.sendStatusMessage(statusMessage, true);
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
    	
    	public String getLang() {
    		switch (this) {
			case PICKUP:
				return "auto_pickup";
			case SEAL:
				return "seal";
			case VOID:
				return "auto_void";
			default:
				return "";
			}
    	}
    	
    	@Nonnull
    	public static ToggleType valueOf(int id) {
    		return id < 0 || id >= values.length ? NULL : values[id];
    	}
    }
}