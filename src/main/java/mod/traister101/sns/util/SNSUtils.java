package mod.traister101.sns.util;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.network.*;
import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;

import net.minecraftforge.fml.ModList;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import java.util.function.IntFunction;

@UtilityClass
public final class SNSUtils {

	public static final String ENABLED = SacksNSuch.MODID + ".enabled";
	public static final String DISABLED = SacksNSuch.MODID + ".disabled";

	public static final boolean CURIOS_LOADED = ModList.get().isLoaded(CuriosApi.MODID);

	public static void sendTogglePacket(final ToggleType toggleType, final boolean flag) {
		SNSPacketHandler.sendToServer(new ServerboundTogglePacket(flag, toggleType));
	}

	public static MutableComponent toggleTooltip(final boolean flag) {
		return flag ?
				Component.translatable(ENABLED).withStyle(ChatFormatting.GREEN) :
				Component.translatable(DISABLED).withStyle(ChatFormatting.RED);
	}

	public static ResourceLocation modLocation(final String name) {
		return new ResourceLocation(SacksNSuch.MODID, name);
	}

	public static MutableComponent intComponent(final int i) {
		return Component.literal(String.valueOf(i));
	}

	public static boolean isCuriosPresent() {
		return CURIOS_LOADED;
	}

	/**
	 * An enum for easy and consistent toggle logic
	 */
	public enum ToggleType {
		NONE(0, "", ""),
		PICKUP(1, SacksNSuch.MODID + ".status.sack.auto_pickup", "pickup");

		private static final IntFunction<ToggleType> BY_ID = ByIdMap.continuous(ToggleType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		@Getter
		public final int id;
		public final String langKey;
		public final String tag;

		ToggleType(final int id, final String langKey, final String tag) {
			this.id = id;
			this.langKey = langKey;
			this.tag = tag;
		}

		public static ToggleType byId(final int toggleTypeId) {
			return BY_ID.apply(toggleTypeId);
		}

		public boolean supportsContainerType(final ContainerType containerType) {
			return switch (this) {
				case NONE -> false;
				case PICKUP -> containerType.doesAutoPickup();
			};
		}

		public MutableComponent getTooltip(final boolean flag) {
			return Component.translatable(langKey, toggleTooltip(flag));
		}
	}
}