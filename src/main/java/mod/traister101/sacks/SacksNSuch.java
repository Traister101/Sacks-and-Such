package mod.traister101.sacks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.PickupHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = SacksNSuch.MODID, name = SacksNSuch.NAME, version = SacksNSuch.VERSION, acceptedMinecraftVersions = SacksNSuch.MCVERSION)
public final class SacksNSuch {
	
	public static final String MODID = "sns";
    public static final String NAME = "Sacks N Such";
    public static final String VERSION = "${version}";
    public static final String MCVERSION = "${mcversion}";
    
	@Instance
	private static SacksNSuch INSTANCE;
	
	private final Logger log = LogManager.getLogger(MODID);
	
	public static Logger getLog() {
		return INSTANCE.log;
	}
	
	public static SacksNSuch getInstance() {
		return INSTANCE;
	}
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	log.info(MODID + " is loading");
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(new PickupHandler());
    }
}