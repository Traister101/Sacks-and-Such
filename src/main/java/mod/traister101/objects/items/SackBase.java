package mod.traister101.objects.items;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

import mod.traister101.Main;
import mod.traister101.util.Reference;
import mod.traister101.util.handlers.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SackBase extends ItemBase
{
	public SackBase(String name) 
	{
		super(name);
	}
	
	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote && !playerIn.isSneaking())
		{
			GuiHandler.openGui(worldIn, playerIn, GuiHandler.Type.SACK_THATCH);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
