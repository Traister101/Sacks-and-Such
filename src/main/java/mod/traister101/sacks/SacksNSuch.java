package mod.traister101.sacks;

import mod.traister101.sacks.client.ClientRegistery;
import mod.traister101.sacks.network.RenamePacket;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.entity.EntitiesSNS;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.PickupHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("FieldMayBeFinal")
@Mod(modid = SacksNSuch.MODID, name = SacksNSuch.NAME, dependencies = "required-after:tfc@1.7.18.176", useMetadata = true)
public final class SacksNSuch {

    public static final String MODID = "sns";
    public static final String NAME = "Sacks N Such";

    @Instance
    private static SacksNSuch INSTANCE = null;

    private final Logger log = LogManager.getLogger(MODID);
    private SimpleNetworkWrapper network;

    public static SimpleNetworkWrapper getNetwork() {
        return INSTANCE.network;
    }

    public static Logger getLog() {
        return INSTANCE.log;
    }

    public static SacksNSuch getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        int id = 0;
        network.registerMessage(new RenamePacket.Handler(), RenamePacket.class, ++id, Side.SERVER);
        network.registerMessage(new TogglePacket.Handler(), TogglePacket.class, ++id, Side.SERVER);
        // Only register pickup handler if auto pickup is enabled
        if (ConfigSNS.GLOBAL.doPickup) {
            MinecraftForge.EVENT_BUS.register(new PickupHandler());
        } else log.info("Sacks of all types won't do autopickup!");

        EntitiesSNS.preInit();

        if (event.getSide().isClient()) {
            ClientRegistery.preInit();
        }
    }
}