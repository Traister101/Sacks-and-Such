package traister101.sacks.util;

import traister101.sacks.util.handlers.GuiHandler;

public enum SackType {

	THATCH(4, 32),
	LEATHER(4, 64),
	BURLAP(4, 128),
	MINERS(1, 512);

	public final int slots;
	public final int slotSize;

	SackType(int slots, int slotSize) {
		this.slots = slots;
		this.slotSize = slotSize;
	}

	public static int getSlotsForType(SackType type) {
		switch (type) {
		case THATCH:
			return SackType.THATCH.slots;
		case LEATHER:
			return SackType.LEATHER.slots;
		case BURLAP:
			return SackType.BURLAP.slots;
		case MINERS:
			return SackType.MINERS.slots;
		default:
			return 4;
		}
	}

	public static GuiHandler.Type getGuiForType(SackType type) {
		switch (type) {
		case THATCH:
			return GuiHandler.Type.THATCH;
		case LEATHER:
			return GuiHandler.Type.LEATHER;
		case BURLAP:
			return GuiHandler.Type.BURLAP;
		case MINERS:
			return GuiHandler.Type.MINERS;
		default:
			return GuiHandler.Type.NULL;
		}
	}
}