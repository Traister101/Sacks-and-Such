package mod.traister101.sacks.util;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import net.dries007.tfc.api.capability.size.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SNSUtils {

	public static void toggle(@Nonnull ItemStack stack, @Nonnull ToggleType type, boolean toggle) {
		final NBTTagCompound tag = NBTHelper.getTagSafely(stack);
		tag.setBoolean(type.key, toggle);
		stack.setTagCompound(tag);
	}

	public static boolean isSmallerOrEqualTo(@Nonnull Size size1, @Nonnull Size size2) {
		return size1.ordinal() <= size2.ordinal();
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	public static <T> T getNull() {
		return null;
	}

	@Nullable
	public static IItemHandler getHandler(@Nonnull ItemStack stack) {
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	/**
	 * @param type Type to toggle
	 */
	public static void sendPacketAndStatus(boolean flag, @Nonnull ToggleType type) {
		SacksNSuch.getNetwork().sendToServer(new TogglePacket(flag, type));
		final String translationKey = SacksNSuch.MODID + type.lang + "." + (flag ? "enabled" : "disabled");
		final TextComponentTranslation statusMessage = new TextComponentTranslation(translationKey);
		Minecraft.getMinecraft().player.sendStatusMessage(statusMessage, true);
	}

	/**
	 * A enum for easy and consistent toggle logic
	 */
	public enum ToggleType {
		SEAL(".explosive_vessel.seal", "seal"),
		VOID(".sack.auto_void", "void"),
		PICKUP(".sack.auto_pickup", "pickup"),
		ITEMS("", "hasItems"),
		NULL("", "");

		public final String lang;
		public final String key;

		ToggleType(String lang, String key) {
			this.lang = lang;
			this.key = key;
		}

		@Nonnull
		public static ToggleType getEmum(int id) {
			return id < 0 || id >= values().length ? NULL : values()[id];
		}
	}
}