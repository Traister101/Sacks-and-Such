package mod.traister101.sacks.util;

import static mod.traister101.sacks.ConfigSNS.BURLAPSACK;
import static mod.traister101.sacks.ConfigSNS.LEATHERSACK;
import static mod.traister101.sacks.ConfigSNS.MINERSACK;
import static mod.traister101.sacks.ConfigSNS.THATCHSACK;

import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;
import net.dries007.tfc.api.capability.size.Size;

public enum SackType {
	THATCH(4),
	LEATHER(4),
	BURLAP(4),
	MINER(1);
	
	private int slots;
	
	SackType(int slots) {
		this.slots = slots;
	}
	
	public static int getSlotCount(SackType type) {
		switch (type) {
		case THATCH:
			return THATCH.slots;
		case LEATHER:
			return LEATHER.slots;
		case BURLAP:
			return BURLAP.slots;
		case MINER:
			return MINER.slots;
		default:
			return 0;
		}
	}
	
	public static int getStackCap(SackType type) {
		switch (type) {
		case THATCH:
			return THATCHSACK.slotCap;
		case LEATHER:
			return LEATHERSACK.slotCap;
		case BURLAP:
			return BURLAPSACK.slotCap;
		case MINER:
			return MINERSACK.slotCap;
		default:
			return 0;
		}
	}
	
	public static boolean isEnabled(SackType type) {
		switch (type) {
		case THATCH:
			return THATCHSACK.isEnabled;
		case LEATHER:
			return LEATHERSACK.isEnabled;
		case BURLAP:
			return BURLAPSACK.isEnabled;
		case MINER:
			return MINERSACK.isEnabled;
		default:
			return false;
		}
	}
	
	public static boolean canTypeDoAutoPickup(SackType type) {
		boolean bool;
		switch (type) {
		case THATCH:
			bool = THATCHSACK.doPickup;
			break;
		case BURLAP:
			bool = BURLAPSACK.doPickup;
			break;
		case LEATHER:
			bool = LEATHERSACK.doPickup;
			break;
		case MINER:
			bool = MINERSACK.doPickup;
			break;
		default:
			bool = false;
		}
		return !bool;
	}
	
	public static GuiType getGui(SackType type) {
		switch (type) {
		case THATCH:
			return GuiType.SACK_THATCH;
		case BURLAP:
			return GuiType.SACK_BURLAP;
		case LEATHER:
			return GuiType.SACK_LEATHER;
		case MINER:
			return GuiType.SACK_MINER;
		default:
			return GuiType.NULL;
		}
	}

	public static Size getSlotSize(SackType type) {
		switch (type) {
		case THATCH:
			return THATCHSACK.allowedSize;
		case BURLAP:
			return BURLAPSACK.allowedSize;
		case LEATHER:
			return LEATHERSACK.allowedSize;
		case MINER:
			return MINERSACK.allowedSize;
		default:
			return Size.NORMAL;
		}
	}
}