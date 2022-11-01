package mod.traister101.sacks.util;

import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;
import net.dries007.tfc.api.capability.size.Size;

import static mod.traister101.sacks.ConfigSNS.SACK;

public enum SackType {
    THATCH(SACK.THATCH_SACK.DANGEROUS.slotCount, SACK.THATCH_SACK.slotCap, SACK.THATCH_SACK.doPickup, SACK.THATCH_SACK.doVoiding, GuiType.SACK_THATCH, SACK.THATCH_SACK.allowedSize),
    LEATHER(SACK.LEATHER_SACK.DANGEROUS.slotCount, SACK.LEATHER_SACK.slotCap, SACK.LEATHER_SACK.doPickup, SACK.LEATHER_SACK.doVoiding, GuiType.SACK_LEATHER, SACK.LEATHER_SACK.allowedSize),
    BURLAP(SACK.BURLAP_SACK.DANGEROUS.slotCount, SACK.BURLAP_SACK.slotCap, SACK.BURLAP_SACK.doPickup, SACK.BURLAP_SACK.doVoiding, GuiType.SACK_BURLAP, SACK.BURLAP_SACK.allowedSize),
    MINER(SACK.MINER_SACK.DANGEROUS.slotCount, SACK.MINER_SACK.slotCap, SACK.MINER_SACK.doPickup, SACK.MINER_SACK.doVoiding, GuiType.SACK_MINER, SACK.MINER_SACK.allowedSize),
    FARMER(SACK.FARMER_SACK.DANGEROUS.slotCount, SACK.FARMER_SACK.slotCap, SACK.FARMER_SACK.doPickup, SACK.FARMER_SACK.doVoiding, GuiType.SACK_FARMER, SACK.FARMER_SACK.allowedSize),
    KNAPSACK(SACK.KNAP_SACK.DANGEROUS.slotCount, SACK.KNAP_SACK.slotCap, SACK.KNAP_SACK.doPickup, SACK.KNAP_SACK.doVoiding, GuiType.SACK_KNAP, SACK.KNAP_SACK.allowedSize);

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