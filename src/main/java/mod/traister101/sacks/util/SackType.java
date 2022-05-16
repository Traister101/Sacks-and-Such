package mod.traister101.sacks.util;

import javax.annotation.Nonnull;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.ConfigSNS.General;

public enum SackType {
	THATCH(4),
	LEATHER(4),
	BURLAP(4),
	MINER(1),
	NULL(0);
	
	private int slots;
	private static final SackType[] values = values();
	
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
		boolean bool = false;
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
		}
		return !bool;
	}
	
	@Nonnull
	public static SackType valueOf(int id) {
		return id < 0 || id >= values.length ? NULL : values[id];
	}
}