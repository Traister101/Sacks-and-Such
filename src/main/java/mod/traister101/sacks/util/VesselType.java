package mod.traister101.sacks.util;

import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;

import javax.annotation.Nonnull;

public enum VesselType {
    EXPLOSIVE(GuiType.VESSEL_EXPLOSIVE),
    STICKY(GuiType.VESSEL_EXPLOSIVE),
    TINY(GuiType.NULL),
    NULL(GuiType.NULL);

    public final GuiType gui;

    VesselType(GuiType gui) {
        this.gui = gui;
    }

    private static final VesselType[] values = values();

    @Nonnull
    public static VesselType valueOf(int id) {
        return id < 0 || id >= values.length ? NULL : values[id];
    }
}