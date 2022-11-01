package mod.traister101.sacks.util.config;

import mod.traister101.sacks.util.config.sack.*;
import net.minecraftforge.common.config.Config;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class SackConfig {

    private static final String LANG_KEY = "config." + MODID;

    @Config.Comment("Config for thatch sack")
    @Config.LangKey(LANG_KEY + ".thatch_sack")
    public final ThatchSack THATCH_SACK = new ThatchSack();

    @Config.Comment("Config for burlap sack")
    @Config.LangKey(LANG_KEY + ".burlap_sack")
    public final BurlapSack BURLAP_SACK = new BurlapSack();

    @Config.Comment("Config for leather sack")
    @Config.LangKey(LANG_KEY + ".leather_sack")
    public final LeatherSack LEATHER_SACK = new LeatherSack();

    @Config.Comment("Config for miners sack")
    @Config.LangKey(LANG_KEY + ".miner_sack")
    public final MinerSack MINER_SACK = new MinerSack();

    @Config.Comment("Config for farmers sack")
    @Config.LangKey(LANG_KEY + ".farmer_sack")
    public final FarmerSack FARMER_SACK = new FarmerSack();

    @Config.Comment("Config for farmers sack")
    @Config.LangKey(LANG_KEY + ".knap_sack")
    public final KnapSack KNAP_SACK = new KnapSack();
}
