package mod.traister101.sacks.util;

import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;

import javax.annotation.Nonnull;

import static mod.traister101.sacks.ConfigSNS.EXPLOSIVE_VESSEL;

public enum VesselType {
	EXPLOSIVE(EXPLOSIVE_VESSEL.DANGEROUS.slotCount, EXPLOSIVE_VESSEL.slotCap, GuiType.VESSEL_EXPLOSIVE, false),
	STICKY(EXPLOSIVE_VESSEL.DANGEROUS.slotCount, EXPLOSIVE_VESSEL.slotCap, GuiType.VESSEL_EXPLOSIVE, true),
	TINY(0, 0, GuiType.NULL, false),
	NULL(0, 0, GuiType.NULL, false);


    public final int slots;
    public final int stackCap;
    public final GuiType gui;
	public final boolean isSticky;

	VesselType(int slots, int stackCap, GuiType gui, boolean isSticky) {
        this.slots = slots;
        this.stackCap = stackCap;
        this.gui = gui;
		this.isSticky = isSticky;
    }

    @Nonnull
    public static VesselType getEmum(int id) {
	    return id < 0 || id >= values().length ? NULL : values()[id];
    }
}