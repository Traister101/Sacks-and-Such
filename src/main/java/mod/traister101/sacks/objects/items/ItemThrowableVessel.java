package mod.traister101.sacks.objects.items;

import mcp.MethodsReturnNonnullByDefault;
import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.entity.projectile.EntityExplosiveVessel;
import mod.traister101.sacks.objects.inventory.capability.VesselHandler;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import mod.traister101.sacks.util.VesselType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemThrowableVessel extends Item implements IItemSize {

    private final VesselType type;
    private VesselHandler handler;

    public ItemThrowableVessel(@Nonnull VesselType type) {
        if (type == VesselType.TINY) {
            setMaxStackSize(16);
        } else setMaxStackSize(1);
        this.type = type;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        final ItemStack heldStack = playerIn.getHeldItem(handIn);
        // Tiny vessel has no gui so throw it immediately
        if (type == VesselType.TINY) {
            throwVessel(worldIn, playerIn, heldStack);
            return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
        }

        if (!playerIn.isSneaking()) {
            if (SNSUtils.isSealed(heldStack)) {
                throwVessel(worldIn, playerIn, heldStack);
                playerIn.addStat(StatList.getObjectUseStats(this));
                return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
            }
        }

        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                SacksNSuch.getNetwork().sendToServer(new TogglePacket(!SNSUtils.isSealed(heldStack), ToggleType.SEAL));
                final String flag = SNSUtils.isSealed(heldStack) ? "disabled" : "enabled";
                final String translationKey = SacksNSuch.MODID + ".explosive_vessel.seal." + flag;
                TextComponentTranslation statusMessage = new TextComponentTranslation(translationKey);
                Minecraft.getMinecraft().player.sendStatusMessage(statusMessage, true);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
            }
            if (!playerIn.isSneaking()) {
                openContainer(worldIn, playerIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, heldStack);
    }

    private void throwVessel(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull ItemStack heldStack) {
        if (!playerIn.capabilities.isCreativeMode) {
            heldStack.shrink(1);
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW,
                SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EntityExplosiveVessel entityVessel = new EntityExplosiveVessel(worldIn, playerIn, calculateStrength(), type);
            entityVessel.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(entityVessel);
        }
    }

    private void openContainer(World worldIn, EntityPlayer playerIn) {
        GuiHandler.openGui(worldIn, playerIn, type.gui);
    }

    private float calculateStrength() {
        if (type == VesselType.TINY) return (float) ConfigSNS.EXPLOSIVE_VESSEL.smallPower;

        final int count = handler.getStackInSlot(0).getCount();
        final double multiplier = ConfigSNS.EXPLOSIVE_VESSEL.explosionMultiplier;
        return (float) ((count / 14) * multiplier);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (type == VesselType.TINY) return;

        String text = SacksNSuch.MODID + ".explosive_vessel.tooltip";
        if (SNSUtils.isSealed(stack)) {
            text += ".sealed";
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
        return SNSUtils.isSealed(stack);
    }

    @Nonnull
    public VesselType getType() {
        return type;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack) {
        return type == VesselType.TINY ? Size.NORMAL : Size.LARGE;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack) {
        return type == VesselType.TINY ? Weight.LIGHT : Weight.VERY_HEAVY;
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack) {
        return type == VesselType.TINY;
    }
}