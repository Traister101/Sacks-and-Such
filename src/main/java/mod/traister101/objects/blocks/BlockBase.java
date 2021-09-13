package mod.traister101.objects.blocks;

import mod.traister101.Main;
import mod.traister101.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block
{
	
	protected String name;
	
	public BlockBase(String name, Material material, float hardness, float resistance, CreativeTabs tab) 
	{
		super(material);
	
		this.name = name;
	
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(hardness);
		setResistance(resistance);
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
		//Default tab
		setCreativeTab(Main.creativeTab);
		
		
		ModBlocks.BLOCKS.add(this);
	}
	
	public void registerItemModel(Item itemBlock) 
	{
		Main.PROXY.registerItemRenderer(itemBlock, 0, name);
	}
	
	public Item createItemBlock() 
	{
		return new ItemBlock(this).setRegistryName(getRegistryName());
	}
	
	@Override
	public BlockBase setCreativeTab(CreativeTabs tab) 
	{
		super.setCreativeTab(tab);
		return this;
	}
}
