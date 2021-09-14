package mod.traister101.util.handlers;
import mod.traister101.Main;
import mod.traister101.blocks.ModBlocks;
import mod.traister101.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class RegistrationHandler 
{
	
	
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelRegister(ModelRegistryEvent event)
	{
		for(Item item : ModItems.ITEMS)
		{
			Main.PROXY.registerItemRenderer(item, 0, "inventory");
		}
		for(Block block : ModBlocks.BLOCKS)
		{
			Main.PROXY.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");
		}
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
	}
	
	public static void preInitRegistries()
	{
		
	}
	
	public static void initRegistries()
	{
		
	}
	
	public static void postInitRegistries()
	{
		
	}
	
	public static void serverRegistries()
	{
		
	}
	
	/*
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.register(event.getRegistry());
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) 
	{
		ModItems.register(event.getRegistry());
		ModBlocks.registerItemBlocks(event.getRegistry());
	}
	
	@SubscribeEvent
	public static void registerItems(ModelRegistryEvent event) {
		ModItems.registerModels();
		ModBlocks.registerModels();
	}
	*/
}