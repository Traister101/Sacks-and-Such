package mod.traister101.sacks.objects.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.util.SackType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.helper.IConfigurable;
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

public class ItemSack extends ItemBase implements IConfigurable {
	
	private final SackType type;
	private Size size;
	private Weight weight;
	
	public ItemSack(String name, SackType type) {
		super(name);
		this.type = type;
		this.size = Size.NORMAL;
		this.weight = Weight.VERY_HEAVY;
	}

//	@Override
//	public int getItemStackLimit() {
//		return 1;
//	}
	
	@Override
	public boolean canStack(ItemStack stack) {
		return false;
	}
		
	public SackType getType() {
		return type;
	}
	
	@Nonnull
	@Override
	public Size getSize(ItemStack stack) {
		// TODO variable size depending on how filled container is
		return size; // No sack-ception
	}
	
	@Nonnull
	@Override
	public Weight getWeight(ItemStack stack) {
		// TODO variable weight depending on how filled contaienr is
		return weight;
	}
	
	public void setSize(Size size) {
		this.size = size;
	}
	
	public void setWeight(Weight weight) {
		this.weight = weight;
	}
	
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote && !playerIn.isSneaking()) {
			GuiHandler.openGui(worldIn, playerIn, type);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new SackHandler(nbt, type, stack);
	}

	@Override
	public boolean isEnabled() {
		return SackType.isEnabled(type);
	}
}