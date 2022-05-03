package traister101.sacks.util;

import traister101.sacks.ConfigSNS;
import traister101.sacks.util.handlers.GuiHandler;

public enum SackType {

	THATCH(4),
	LEATHER(4),
	BURLAP(4),
	MINER(1);
	
	public final int slots;
	
	SackType(int slots) {
		this.slots = slots;
	}
	
	public static int getSlotsForType(SackType type) {
		switch (type) {
		case THATCH:
			return SackType.THATCH.slots;
		case LEATHER:
			return SackType.LEATHER.slots;
		case BURLAP:
			return SackType.BURLAP.slots;
		case MINER:
			return SackType.MINER.slots;
		default:
			return 4;
		}
	}
	
	public static int getSlotLimitForType(SackType type) {
		switch (type) {
		case THATCH:
			return ConfigSNS.General.THATCHSACK.slotCap;
		case LEATHER:
			return ConfigSNS.General.LEATHERSACK.slotCap;
		case BURLAP:
			return ConfigSNS.General.BURLAPSACK.slotCap;
		case MINER:
			return ConfigSNS.General.MINERSACK.slotCap;
		default:
			return 32;
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
		case MINER:
			return GuiHandler.Type.MINERS;
		default:
			return GuiHandler.Type.NULL;
		}
	}
}