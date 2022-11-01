package mod.traister101.sacks.util;

import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;

import javax.annotation.Nonnull;

import static mod.traister101.sacks.ConfigSNS.EXPLOSIVE_VESSEL;

public enum VesselType {
    EXPLOSIVE(EXPLOSIVE_VESSEL.DANGEROUS.slotCount, EXPLOSIVE_VESSEL.slotCap, GuiType.VESSEL_EXPLOSIVE),
    STICKY(EXPLOSIVE_VESSEL.DANGEROUS.slotCount, EXPLOSIVE_VESSEL.slotCap, GuiType.VESSEL_EXPLOSIVE),
    TINY(0, 0, GuiType.NULL),
    NULL(0, 0, GuiType.NULL);


    public final int slots;
    public final int stackCap;
    public final GuiType gui;

    VesselType(int slots, int stackCap, GuiType gui) {
        this.slots = slots;
        this.stackCap = stackCap;
        this.gui = gui;
    }

    private static final VesselType[] values = values();

    @Nonnull
    public static VesselType valueOf(int id) {
        return id < 0 || id >= values.length ? NULL : values[id];
    }
}