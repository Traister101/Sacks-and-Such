package mod.traister101.sacks.util;

import static mod.traister101.sacks.ConfigSNS.EXPLOSIVE_VESSEL;

import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;

public enum VesselType {
	EXPLOSIVE,
	STICKY;
	
	
	public static GuiType getGui(VesselType type) {
		switch (type) {
		case EXPLOSIVE:
		case STICKY:
			return GuiHandler.GuiType.VESSEL_EXPLOSIVE;
		default:
			return GuiHandler.GuiType.NULL;
		}
	}

	public static boolean isEnabled(VesselType type) {
		switch (type) {
		case EXPLOSIVE:
			return EXPLOSIVE_VESSEL.enabled;
		case STICKY:
			return EXPLOSIVE_VESSEL.stickyEnabled && EXPLOSIVE_VESSEL.enabled;
		default:
			return false;
		}
	}
}