package mod.traister101.sacks.objects.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.objects.entity.projectile.EntityExplosiveVessel;
import mod.traister101.sacks.objects.inventory.capability.VesselHandler;
import mod.traister101.sacks.util.VesselType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import mod.traister101.sacks.util.handlers.GuiHandler.GuiType;
import mod.traister101.sacks.util.helper.IConfigurable;
import mod.traister101.sacks.util.helper.IToggleAble;
import mod.traister101.sacks.util.helper.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemThrowableVessel extends ItemSNS implements IConfigurable, IToggleAble {
	
	private final boolean isSticky;
	private final VesselType type;
	
	private VesselHandler handler;
	
	public ItemThrowableVessel(String name, VesselType type) {
		super(name);
		this.type = type;
		this.maxStackSize = 1;
		if (type == VesselType.STICKY) {
			this.isSticky = true;
		} else this.isSticky = false;
	}
	
	public VesselType getType() {
		return type;
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		final ItemStack heldStack = playerIn.getHeldItem(handIn);
		final GuiType gui = VesselType.getGui(type);
		
		// TODO not being sealed should always open the gui so no empty vessels can be thrown
		if (!Utils.isSealed(heldStack)) {
			if (!worldIn.isRemote) {
				GuiHandler.openGui(worldIn, playerIn, gui);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
		}
		
		if (playerIn.isSneaking()) {
			if (!worldIn.isRemote) {
				GuiHandler.openGui(worldIn, playerIn, gui);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
		}
		
		final float strength = calculateStrength();
		if (strength <= 0) {
			if (!worldIn.isRemote) {
				GuiHandler.openGui(worldIn, playerIn, gui);
			}
			return new ActionResult<>(EnumActionResult.FAIL, heldStack);
		}
		
		if (!playerIn.capabilities.isCreativeMode) {
			heldStack.shrink(1);
		}
		
		worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ,
				SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F,
				0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		
		if (!worldIn.isRemote) {
			EntityExplosiveVessel entityVessel = new EntityExplosiveVessel(worldIn, playerIn, strength, isSticky);
			entityVessel.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityVessel);
		}
		playerIn.addStat(StatList.getObjectUseStats(this));
		return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
	}
	
	private float calculateStrength() {
		final int count = handler.getStackInSlot(0).getCount();
		final float multiplier = ConfigSNS.General.EXPLOSIVE_VESSEL.explosionMultiplier;
		final float strength = (count / 10) * multiplier;
		return strength;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String text = SacksNSuch.MODID + ".explosive_vessel.tooltip";
		if (GuiScreen.isShiftKeyDown()) {
			if (Utils.isSealed(stack)) {
				text += ".sealed";
			}
			text += ".shift";
		}
		tooltip.add(I18n.format(text));
	}
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		handler = new VesselHandler(nbt, type);
		return handler;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && Utils.isSealed(stack);
	}
	
	public VesselHandler getHandler() {
		return handler;
	}
	
	@Override
	public boolean isEnabled() {
		return VesselType.isEnabled(type);
	}
}