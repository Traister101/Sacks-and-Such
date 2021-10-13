package mod.traister101;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.traister101.client.ModTab;
import mod.traister101.proxy.CommonProxy;
import mod.traister101.util.Reference;
import mod.traister101.util.handlers.GuiHandler;
import mod.traister101.util.handlers.RegistryHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class Main
{
	
	//Sets up mod instance
	@Instance
	private static Main INSTANCE;

	//Sets up mod Logger
	private final Logger log = LogManager.getLogger(Reference.MODID);
	
		
	//Sets up mod creative tab
	public static final ModTab creativeTab = new ModTab();
	
	
	public static Logger getLog()
	{
		return INSTANCE.log;
	}
	
	public static Main getInstance()
	{
		return INSTANCE;
	}
	
	//Proxy
    @SidedProxy(modId = Reference.MODID, clientSide = Reference.CLIENT, serverSide = Reference.COMMON)
    public static CommonProxy PROXY;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	log.info(Reference.MODID + " is loading");
    	RegistryHandler.preInitRegistry();
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	RegistryHandler.initRegistry();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	RegistryHandler.postInitRegistry();
    }
    
    public void serverInit(FMLServerStartingEvent event)
    {
    	RegistryHandler.serverRegistry();
    }
    
}