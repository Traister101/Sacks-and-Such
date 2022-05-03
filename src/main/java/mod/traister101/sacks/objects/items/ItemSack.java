package mod.traister101.sacks.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.objects.inventory.capability.SackCapability;
import mod.traister101.sacks.util.SackType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemSack extends ItemBase {

	private final SackType type;

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
		return new SackCapability(nbt, type, SackType.getSlotLimitForType(type));
	}
}