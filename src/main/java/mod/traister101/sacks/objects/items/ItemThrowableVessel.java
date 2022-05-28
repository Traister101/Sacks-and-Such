package mod.traister101.sacks.objects.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.objects.entity.projectile.EntityExplosiveVessel;
import mod.traister101.sacks.util.VesselType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.helper.IConfigurable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemThrowableVessel extends ItemSNS implements IConfigurable {
	
	private final VesselType type;
	private EntityPlayer player;
	private boolean sealed;
	
	public ItemThrowableVessel(String name, VesselType type) {
		super(name);
		this.type = type;
		this.maxStackSize = 1;
	}
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack heldStack = playerIn.getHeldItem(handIn);
		
		// TODO not being sealed should always open the gui so no empty vessels can be thrown
		if (!sealed) {
			if (!worldIn.isRemote && !playerIn.isSneaking()) {
//				GuiHandler.openGui(worldIn, playerIn, VesselType.getGui(type));
			}
//			return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
		}
		
		if (!playerIn.capabilities.isCreativeMode) {
			heldStack.shrink(1);
		}
		
		worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ,
				SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F,
				0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		
		if (!worldIn.isRemote) {
			EntityExplosiveVessel entityVessel = new EntityExplosiveVessel(worldIn, playerIn, 5);
			entityVessel.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityVessel);
		}
		playerIn.addStat(StatList.getObjectUseStats(this));
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
	
	public boolean isSealed() {
		return sealed;
	}
	
	public void setSeal(boolean newSeal) {
		this.sealed = newSeal;
	}
	
	@Override
	public boolean isEnabled() {
		return VesselType.isEnabled(type);
	}
}