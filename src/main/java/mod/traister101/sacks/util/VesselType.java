package mod.traister101.sacks.util;

import static mod.traister101.sacks.ConfigSNS.EXPLOSIVE_VESSEL;

import javax.annotation.Nonnull;

import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;

public enum VesselType {
	EXPLOSIVE,
	STICKY,
	TINY,
	NULL;
	
	private static final VesselType[] values = values();
	
	public static GuiType getGui(VesselType type) {
		switch (type) {
		case EXPLOSIVE:
		case STICKY:
			return GuiHandler.GuiType.VESSEL_EXPLOSIVE;
		case TINY:
		default:
			return GuiHandler.GuiType.NULL;
		}
	}

	public static boolean isEnabled(VesselType type) {
		switch (type) {
		case EXPLOSIVE:
			return EXPLOSIVE_VESSEL.isEnabled;
		case STICKY:
			return EXPLOSIVE_VESSEL.stickyEnabled && EXPLOSIVE_VESSEL.isEnabled;
		case TINY:
			return EXPLOSIVE_VESSEL.smallEnabled;
		default:
			return false;
		}
	}
	
	@Nonnull
	public static VesselType valueOf(int id) {
		return id < 0 || id >= values.length ? NULL : values[id];
	}
}