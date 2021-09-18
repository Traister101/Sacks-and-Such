package mod.traister101.objects.items;

import javax.annotation.Nonnull;

import mod.traister101.util.handlers.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SackBase extends ItemBase
{
	private final GuiHandler.Type GUI;
	
	public SackBase(String name, GuiHandler.Type gui) 
	{
		super(name);
		this.GUI = gui;
	}
	
	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote)
		{
			GuiHandler.openGui(worldIn, playerIn, GUI);
			System.out.println("Should have the GUI");
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
	
}