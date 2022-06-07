package mod.traister101.sacks.util;

import mod.traister101.sacks.ConfigSNS;
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
			return ConfigSNS.General.EXPLOSIVE_VESSEL.enabled;
		case STICKY:
			return ConfigSNS.General.EXPLOSIVE_VESSEL.stickyEnabled;
		default:
			return false;
		}
	}
}