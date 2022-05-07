package mod.traister101.sacks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.traister101.sacks.client.SNSTab;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.PickupHandler;
import mod.traister101.sacks.util.handlers.RegistryHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = SacksNSuch.MODID, name = SacksNSuch.NAME, version = SacksNSuch.VERSION, dependencies = SacksNSuch.DEPENDENCIES)
public class SacksNSuch {
	
	public static final String MODID = "sns";
    public static final String NAME = "Sacks N Such";
    public static final String VERSION = "@version@";
    public static final String DEPENDENCIES = "required-after:forge" /*"required-after:tfc"*/;
	
	//Sets up mod instance
	@Instance
	private static SacksNSuch INSTANCE;

	//Sets up mod Logger
	private final Logger log = LogManager.getLogger(MODID);
	
		
	//Sets up mod creative tab
	public static final SNSTab creativeTab = new SNSTab();
	
	
	public static Logger getLog() {
		return INSTANCE.log;
	}
	
	public static SacksNSuch getInstance() {
		return INSTANCE;
	}
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	log.info(MODID + " is loading");
    	RegistryHandler.preInitRegistry();
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(new PickupHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	RegistryHandler.initRegistry();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	RegistryHandler.postInitRegistry();
    }
    
    public void serverInit(FMLServerStartingEvent event) {
    	RegistryHandler.serverRegistry();
    }
    
}