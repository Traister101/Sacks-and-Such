package traister101.sacks.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
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
import traister101.sacks.util.SackType;
import traister101.sacks.util.handlers.GuiHandler;

public class ItemSack extends ItemBase {

	private SackType type;
	private Size size;

	public ItemSack(String name, SackType type) {
		super(name);
		this.type = type;
	}

	@Nonnull
	@Override
	public Size getSize(ItemStack stack) {

		// TODO variable size depending on how filled container is
		return Size.NORMAL; // No sack-ception
	}

	@Nonnull
	@Override
	public Weight getWeight(ItemStack stack) {
		return Weight.VERY_HEAVY; // One per stack
	}

	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote && !playerIn.isSneaking()) {
			GuiHandler.openGui(worldIn, playerIn, SackType.getGuiForType(type));
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new SackCapability(nbt, type);
	}

	public static class SackCapability extends ItemStackHandler implements ICapabilityProvider, ISlotCallback {

		SackCapability(@Nullable NBTTagCompound nbt, SackType type) {
			super(SackType.getSlotsForType(type));

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

		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			IItemSize size = CapabilityItemSize.getIItemSize(stack);
			if (size != null) {
				return size.getSize(stack).isSmallerThan(Size.NORMAL);
			}
			return false;
		}
	}
}