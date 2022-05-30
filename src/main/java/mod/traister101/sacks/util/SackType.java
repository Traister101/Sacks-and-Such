package mod.traister101.sacks.util;

import javax.annotation.Nonnull;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.ConfigSNS.General;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;

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
			return ConfigSNS.General.THATCHSACK.slotCap;
		case LEATHER:
			return ConfigSNS.General.LEATHERSACK.slotCap;
		case BURLAP:
			return ConfigSNS.General.BURLAPSACK.slotCap;
		case MINER:
			return ConfigSNS.General.MINERSACK.slotCap;
		default:
			return 0;
		}
	}
	
	public static boolean isEnabled(SackType type) {
		switch (type) {
		case THATCH:
			return ConfigSNS.General.THATCHSACK.enabled;
		case LEATHER:
			return ConfigSNS.General.LEATHERSACK.enabled;
		case BURLAP:
			return ConfigSNS.General.BURLAPSACK.enabled;
		case MINER:
			return ConfigSNS.General.MINERSACK.enabled;
		default:
			return false;
		}
	}
	
	// Should always always have a type
	@SuppressWarnings("incomplete-switch")
	public static boolean getPickupConfig(SackType type) {
		boolean bool;
		switch (type) {
		case THATCH:
			bool = General.THATCHSACK.pickup;
			break;
		case BURLAP:
			bool = General.BURLAPSACK.pickup;
			break;
		case LEATHER:
			bool = General.LEATHERSACK.pickup;
			break;
		case MINER:
			bool = General.MINERSACK.pickup;
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
}