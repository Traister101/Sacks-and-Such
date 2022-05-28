package mod.traister101.sacks.util;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.GuiHandler.Type;

public enum VesselType {
	EXPLOSIVE;
	
	
	public static Type getGui(VesselType type) {
		switch (type) {
		case EXPLOSIVE:
			return GuiHandler.Type.VESSEL_EXPLOSIVE;
		default:
			return GuiHandler.Type.NULL;
		}
	}

	public static boolean isEnabled(VesselType type) {
		switch (type) {
		case EXPLOSIVE:
			return ConfigSNS.General.EXPLOSIVE_VESSEL.enabled;
		default:
			return false;
		}
	}
}