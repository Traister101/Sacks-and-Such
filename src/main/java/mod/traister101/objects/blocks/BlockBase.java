package mod.traister101.objects.blocks;

import mod.traister101.Main;
import mod.traister101.blocks.ModBlocks;
import mod.traister101.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block
{
	
	protected String name;
	
	public BlockBase(String name, Material material, float hardness, float resistance, int miningLevel, String tool, CreativeTabs tab) 
	{
		super(material);
	
		this.name = name;
	
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(hardness);
		setResistance(resistance);
		setHarvestLevel(tool, miningLevel);
		setCreativeTab(tab);
		
		
		ModBlocks.BLOCKS.add(this);
	}
	
	public BlockBase(String name) 
	{
		super(Material.IRON);
	
		this.name = name;
	
		setTranslationKey(name);
		setRegistryName(name);
		//Default hardness
		setHardness(10f);
		//Default resistance
		setResistance(10f);
		//Default harvest level
		setHarvestLevel("", 10);
		//Default tab
		setCreativeTab(Main.creativeTab);
		
		
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
	
	@Override
	public BlockBase setCreativeTab(CreativeTabs tab) 
	{
		super.setCreativeTab(tab);
		return this;
	}
}
