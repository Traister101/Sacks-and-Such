package mod.traister101.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.objects.container.EnumSackType;
import mod.traister101.util.handlers.ConfigHandler;
import mod.traister101.util.handlers.GuiHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
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

public class ItemSack extends ItemBase {
	
	private static EnumSackType TYPE;
	
	public ItemSack(String name, EnumSackType type) {
		
		super(name);
		this.TYPE = type;
	}
	
	 @Override
	 public boolean canStack(@Nonnull ItemStack stack) {
		 return false;
	 }
	 
	 @Nonnull
	 @Override
	 public Size getSize(ItemStack stack) {
		 //TODO variable size depending on how filled container is
		 return Size.SMALL;
	 }
	 
	 @Nonnull
	 @Override
	 public Weight getWeight(ItemStack stack) {
		 //TODO variable weight depending on how filled container is 
		 return Weight.LIGHT;
	 }
     
	 @Nonnull
	 public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		 ItemStack stack = playerIn.getHeldItem(handIn);
		 if (!worldIn.isRemote && !playerIn.isSneaking()) {
			 GuiHandler.openGui(worldIn, playerIn, getGuiForType(TYPE));
		 }
		 return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	 }
	
	public int getSackSize() {
		return getSlotsForType(TYPE);
	}
	
	private static int getSlotsForType(EnumSackType type) {
		switch (type) {
			case THATCH:
				return ConfigHandler.General.THATCHSACK.slots;
			case LEATHER:
				return ConfigHandler.General.LEATHERSACK.slots;
			case BURLAP:
			default:
				return ConfigHandler.General.BURLAPSACK.slots;
		}
	}
	
	private static GuiHandler.Type getGuiForType(EnumSackType type) {
		switch (type) {
		case THATCH:
			return GuiHandler.Type.SACK_THATCH;
		case LEATHER:
			return GuiHandler.Type.SACK_LEATHER;
		case BURLAP:
		default:
			return GuiHandler.Type.SACK_BURLAP;
		}
	}
	
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new SackCapability(nbt);
    }
    
    // Extends ItemStackHandler for ease of use.
    public static class SackCapability extends ItemStackHandler implements ICapabilityProvider, ISlotCallback {

    	SackCapability(@Nullable NBTTagCompound nbt) {
    		super(getSlotsForType(TYPE));

    		if (nbt != null) {
    			deserializeNBT(nbt);
    		}
    	}

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? (T) this : null;
        }
    }
}