package mod.traister101;

import org.apache.logging.log4j.Logger;

import mod.traister101.client.ModTab;
import mod.traister101.proxy.CommonProxy;
import mod.traister101.util.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class Main
{
	
	//Sets up mod creative tab
	public static final ModTab creativeTab = new ModTab();

	//Sets up mod Logger
    private static Logger logger;
	
    //Sets up mod instance using Mod ID
	@Mod.Instance(Reference.MODID)
	public static Main instance;
	
	//Proxy
    @SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.COMMON)
    public static CommonProxy PROXY;
    
    //Things that need to be done during loading
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	logger.info(Reference.MODID + " is loading");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    	
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    
}