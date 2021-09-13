package mod.traister101.blocks;

import java.util.ArrayList;
import java.util.List;

import mod.traister101.Main;
import mod.traister101.objects.blocks.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks 
{
	//Makes an array that all the blocks go in
	public static final List<Block> BLOCKS = new ArrayList();
	
	public static BlockBase TEST_BLOCK = new BlockBase("test_block", Material.IRON, 3f, 3f, Main.creativeTab);
	
	public static void register(IForgeRegistry<Block> registry) 
	{
		registry.registerAll(
				TEST_BLOCK
		);
	}

	public static void registerItemBlocks(IForgeRegistry<Item> registry) 
	{
		registry.registerAll(
				TEST_BLOCK.createItemBlock()
		);
	}

	public static void registerModels() 
	{
		TEST_BLOCK.registerItemModel(Item.getItemFromBlock(TEST_BLOCK));
	}
}
