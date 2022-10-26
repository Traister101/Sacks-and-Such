package mod.traister101.sacks.util;

import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;
import net.dries007.tfc.api.capability.size.Size;

import static mod.traister101.sacks.ConfigSNS.*;

public enum SackType {
    THATCH(THATCH_SACK.DANGEROUS.slotCount, THATCH_SACK.slotCap, THATCH_SACK.doPickup, THATCH_SACK.doVoiding, GuiType.SACK_THATCH, THATCH_SACK.allowedSize),
    LEATHER(LEATHER_SACK.DANGEROUS.slotCount, LEATHER_SACK.slotCap, LEATHER_SACK.doPickup, LEATHER_SACK.doVoiding, GuiType.SACK_LEATHER, LEATHER_SACK.allowedSize),
    BURLAP(BURLAP_SACK.DANGEROUS.slotCount, BURLAP_SACK.slotCap, BURLAP_SACK.doPickup, BURLAP_SACK.doVoiding, GuiType.SACK_BURLAP, BURLAP_SACK.allowedSize),
    MINER(MINER_SACK.DANGEROUS.slotCount, MINER_SACK.slotCap, MINER_SACK.doPickup, MINER_SACK.doVoiding, GuiType.SACK_MINER, MINER_SACK.allowedSize),
    FARMER(FARMER_SACK.DANGEROUS.slotCount, FARMER_SACK.slotCap, FARMER_SACK.doPickup, FARMER_SACK.doVoiding, GuiType.SACK_FARMER, FARMER_SACK.allowedSize),
    KNAPSACK(KNAP_SACK.DANGEROUS.slotCount, KNAP_SACK.slotCap, KNAP_SACK.doPickup, KNAP_SACK.doVoiding, GuiType.SACK_KNAP, KNAP_SACK.allowedSize);

    public final int slots;
    public final int stackCap;
    public final boolean doesAutoPickup;
    public final boolean doesVoiding;
    public final GuiType gui;
    public final Size allowedSize;

    SackType(int slots, int stackCap, boolean doAutoPickup, boolean doesVoiding, GuiType gui, Size allowedSize) {
        this.slots = slots;
        this.stackCap = stackCap;
        this.doesAutoPickup = doAutoPickup;
        this.doesVoiding = doesVoiding;
        this.gui = gui;
        this.allowedSize = allowedSize;
    }
}