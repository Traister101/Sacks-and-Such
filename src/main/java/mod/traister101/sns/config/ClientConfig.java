package mod.traister101.sns.config;

import mod.traister101.sns.client.ClientLunchboxTooltip;
import mod.traister101.sns.common.items.HikingBootsItem.BootModelType;

import net.minecraft.Util;

import net.minecraftforge.common.ForgeConfigSpec.*;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import java.util.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public final class ClientConfig {

	BooleanValue voidGlint;
	BooleanValue displayItemContentsAsImages;
	ConfigValue<List<? extends String>> openItemContainerCuriosPriorities;
	EnumValue<BootModelType> bootModelType;
	ConfigValue<String> lunchboxSelectedSlotHighlightColor;
	IntValue lunchboxSelectedSlotHighlightThickness;

	ClientConfig(final Builder builder) {
		voidGlint = builder.comment("Swaps the enchant glint from when auto pickup is enabled to when it's disabled").define("voidGlint", true);
		displayItemContentsAsImages = builder.comment("When enabled sacks will display their contents like how TFC vessels do")
				.define("displayItemContentsAsImages", true);
		openItemContainerCuriosPriorities = builder.comment(
						"A list of curio slot identifiers ordered by highest priority to lowest priority. The default config will skip over anything in the back slot (useful when wearing a quiver). Additionally for you pack devs this is the actual curio slot identifier meaning custom slots will Just WorkTM")
				.defineList("openItemContainerCuriosPriorities", () -> Util.make(new ArrayList<>(), l -> {
					l.add("belt");
					l.add("back");
				}), String.class::isInstance);
		bootModelType = builder.comment(
						"Config for which hiking boots model is used. FANCY for the full 3D model, NO_FLOOF for only toes and VANILLA for the vanilla style model")
				.defineEnum("bootModelType", BootModelType.FANCY);
		lunchboxSelectedSlotHighlightColor = builder.comment(
						"The slot highlight color for the lunchbox item contents tooltip in hexadecimal in RGB format (technically ARGB but the alpha value will be ignored)")
				.define("lunchboxSelectedSlotHighlightColor", Integer.toHexString(ClientLunchboxTooltip.DEFAULT_COLOR));
		lunchboxSelectedSlotHighlightThickness = builder.comment(
						"The line thickness in pixels, 0 will effectively disable the highlight 7 will make it fill the whole slot")
				.defineInRange("lunchboxSelectedSlotHighlightThickness", 1, 0, 7);
	}
}