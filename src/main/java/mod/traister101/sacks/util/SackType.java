package mod.traister101.sacks.util;

import static mod.traister101.sacks.ConfigSNS.General.BURLAPSACK;
import static mod.traister101.sacks.ConfigSNS.General.LEATHERSACK;
import static mod.traister101.sacks.ConfigSNS.General.MINERSACK;
import static mod.traister101.sacks.ConfigSNS.General.THATCHSACK;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;
import net.dries007.tfc.api.capability.size.Size;

public enum SackType {
	THATCH(4),
	LEATHER(4),
	BURLAP(4),
	MINER(1),
	NULL(0);
	
	private int slots;
	
	SackType(int slots) {
		this.slots = slots;
	}
	
	public static int getSlotCount(SackType type) {
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
			return THATCHSACK.enabled;
		case LEATHER:
			return LEATHERSACK.enabled;
		case BURLAP:
			return BURLAPSACK.enabled;
		case MINER:
			return MINERSACK.enabled;
		default:
			return false;
		}
	}
	
	public static boolean canTypeDoAutoPickup(SackType type) {
		boolean bool;
		switch (type) {
		case THATCH:
			bool = THATCHSACK.pickup;
			break;
		case BURLAP:
			bool = BURLAPSACK.pickup;
			break;
		case LEATHER:
			bool = LEATHERSACK.pickup;
			break;
		case MINER:
			bool = MINERSACK.pickup;
			break;
		default:
			bool = false;
		}
		return !bool;
	}
	
	public static GuiType getGui(SackType type) {
		switch (type) {
		case THATCH:
			return GuiHandler.GuiType.SACK_THATCH;
		case BURLAP:
			return GuiHandler.GuiType.SACK_BURLAP;
		case LEATHER:
			return GuiHandler.GuiType.SACK_LEATHER;
		case MINER:
			return GuiHandler.GuiType.SACK_MINER;
		default:
			return GuiHandler.GuiType.NULL;
		}
	}

	public static Size getSlotSize(SackType type) {
		String size = "";
		switch (type) {
		case THATCH:
			size = THATCHSACK.allowedSize;
			break;
		case BURLAP:
			size = BURLAPSACK.allowedSize;
			break;
		case LEATHER:
			size = LEATHERSACK.allowedSize;
			break;
		case MINER:
			size = MINERSACK.allowedSize;
			break;
		default:
			size = "normal";
			break;
		}
		return Size.valueOf(size.toUpperCase());
	}
}