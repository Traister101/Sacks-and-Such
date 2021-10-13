package mod.traister101.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.util.handlers.GuiHandler;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemSack extends ItemBase
{
	private GuiHandler.Type GUI;
	private static int SLOTS;
	
	public ItemSack(String name, int slots, GuiHandler.Type gui) 
	{
		super(name);
		this.GUI = gui;
		this.SLOTS = slots;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote && !playerIn.isSneaking())
		{
			GuiHandler.openGui(worldIn, playerIn, GUI);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
	
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new QuiverCapability(nbt);
    }

    // Extends ItemStackHandler for ease of use.
    public static class QuiverCapability extends ItemStackHandler implements ICapabilityProvider, ISlotCallback
    {

        QuiverCapability(@Nullable NBTTagCompound nbt)
        {
            super(SLOTS);

            if (nbt != null)
            {
                deserializeNBT(nbt);
            }
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) this : null;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
        	  IItemSize size = CapabilityItemSize.getIItemSize(stack);
              if (size != null)
              {
                  return size.getSize(stack).isSmallerThan(Size.NORMAL);
              }
              return false;
        }
    }
}