package mod.traister101.sacks.util;

import static mod.traister101.sacks.ConfigSNS.BURLAP_SACK;
import static mod.traister101.sacks.ConfigSNS.LEATHER_SACK;
import static mod.traister101.sacks.ConfigSNS.MINER_SACK;
import static mod.traister101.sacks.ConfigSNS.THATCH_SACK;

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
			return THATCH_SACK.slotCap;
		case LEATHER:
			return LEATHER_SACK.slotCap;
		case BURLAP:
			return BURLAP_SACK.slotCap;
		case MINER:
			return MINER_SACK.slotCap;
		default:
			return 0;
		}
	}
	
	public static boolean canTypeDoAutoPickup(SackType type) {
		boolean bool;
		switch (type) {
		case THATCH:
			bool = THATCH_SACK.doPickup;
			break;
		case BURLAP:
			bool = BURLAP_SACK.doPickup;
			break;
		case LEATHER:
			bool = LEATHER_SACK.doPickup;
			break;
		case MINER:
			bool = MINER_SACK.doPickup;
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
			return THATCH_SACK.allowedSize;
		case BURLAP:
			return BURLAP_SACK.allowedSize;
		case LEATHER:
			return LEATHER_SACK.allowedSize;
		case MINER:
			return MINER_SACK.allowedSize;
		default:
			return Size.NORMAL;
		}
	}
}