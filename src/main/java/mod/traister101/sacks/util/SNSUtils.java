package mod.traister101.sacks.util;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import net.dries007.tfc.api.capability.size.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public final class SNSUtils {

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

    public static void toggle(@Nonnull ItemStack stack, @Nonnull ToggleType type, boolean toggle) {
        final NBTTagCompound tag = getTagSafely(stack);
        tag.setBoolean(type.key, toggle);
        stack.setTagCompound(tag);
    }

    @Nonnull
    public static NBTTagCompound getTagSafely(@Nonnull ItemStack stack) {
        return stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
    }

    public static boolean isSmallerOrEqualTo(@Nonnull Size size1, @Nonnull Size size2) {
        return size1.ordinal() <= size2.ordinal();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T getNull() {
        return null;
    }

    public static IItemHandler getHandler(@Nonnull ItemStack stack) {
        return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public static void sendPacketAndStatus(boolean flag, @Nonnull ToggleType type) {
        SacksNSuch.getNetwork().sendToServer(new TogglePacket(flag, type));
        TextComponentTranslation statusMessage = new TextComponentTranslation(SacksNSuch.MODID + ".sack." + type.lang + "." + (flag ? "enabled" : "disabled"));
        Minecraft.getMinecraft().player.sendStatusMessage(statusMessage, true);
    }

    public enum ToggleType {
        SEAL("seal", "seal"),
        VOID("auto_void", "void"),
        PICKUP("auto_pickup", "pickup"),
        ITEMS("", "hasItems"),
        NULL("", "");

        public final String lang;
        public final String key;

        ToggleType(String lang, String key) {
            this.lang = lang;
            this.key = key;
        }

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