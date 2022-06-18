package mod.traister101.sacks.util;

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
	
	@Nonnull
	public static VesselType valueOf(int id) {
		return id < 0 || id >= values.length ? NULL : values[id];
	}
}