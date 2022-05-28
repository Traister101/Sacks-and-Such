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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemSack extends ItemSNS implements IConfigurable {
	
	private SackHandler handler;
	private final SackType type;
	
	public ItemSack(String name, SackType type) {
		super(name);
		this.type = type;
		this.size = Size.NORMAL;
		this.weight = Weight.VERY_HEAVY;
		this.maxStackSize = 1;
	}
	
	@Override
	public boolean canStack(ItemStack stack) {
		return false;
	}
	
	@Nonnull
	public SackType getType() {
		return type;
	}
	
	public void setSize(Size size) {
		this.size = size;
	}
	
	public void setWeight(Weight weight) {
		this.weight = weight;
	}
	
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack heldStack = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote && !playerIn.isSneaking()) {
			GuiHandler.openGui(worldIn, playerIn, SackType.getGui(type));
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String text = TextFormatting.GRAY + getTranslationKey() + ".tooltip";
		if (GuiScreen.isShiftKeyDown()) {
			text = text + ".shift";
		}
		tooltip.add(text);
	}
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		handler = new SackHandler(nbt, type);
		return handler;
	}
	
	public SackHandler getHandler() {
		return handler;
	}
	
	@Override
	public boolean isEnabled() {
		return SackType.isEnabled(type);
	}
}