package mod.traister101.sacks.objects.items;

import mcp.MethodsReturnNonnullByDefault;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.objects.entity.projectile.EntityExplosiveVessel;
import mod.traister101.sacks.objects.inventory.capability.VesselHandler;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import mod.traister101.sacks.util.VesselType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static mod.traister101.sacks.ConfigSNS.EXPLOSIVE_VESSEL;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemThrowableVessel extends Item implements IItemSize {

    private final VesselType type;

    public ItemThrowableVessel(@Nonnull VesselType type) {
        if (type == VesselType.TINY) {
            setMaxStackSize(16);
        } else setMaxStackSize(1);
        this.type = type;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer playerIn, final EnumHand handIn) {
        final ItemStack heldStack = playerIn.getHeldItem(handIn);
        // Tiny vessel has no gui so throw it immediately
        if (type == VesselType.TINY) {
            throwVessel(worldIn, playerIn, heldStack);
            return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
        }

        if (!playerIn.isSneaking()) {
            if (SNSUtils.isSealed(heldStack)) {
                throwVessel(worldIn, playerIn, heldStack);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
            }
        }

        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                SNSUtils.sendPacketAndStatus(!SNSUtils.isSealed(heldStack), ToggleType.SEAL);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
            }
            if (!playerIn.isSneaking()) {
                openContainer(worldIn, playerIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, heldStack);
    }

    private void throwVessel(final World worldIn, final EntityPlayer playerIn, final ItemStack heldStack) {
        final IItemHandler containerInv = SNSUtils.getHandler(heldStack);
        if (!playerIn.capabilities.isCreativeMode)
            heldStack.shrink(1);

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW,
                SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            final EntityExplosiveVessel entityVessel = new EntityExplosiveVessel(worldIn, playerIn, calculateStrength(containerInv), type);
            entityVessel.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(entityVessel);
        }
    }

    private void openContainer(World worldIn, EntityPlayer playerIn) {
        GuiHandler.openGui(worldIn, playerIn, type.gui);
    }

    private float calculateStrength(final IItemHandler containerInv) {
        if (type == VesselType.TINY) return (float) EXPLOSIVE_VESSEL.smallPower;

        int count = 0;
        for (int i = 0; i < containerInv.getSlots(); i++) {
            count += containerInv.getStackInSlot(i).getCount();
            if (count > EXPLOSIVE_VESSEL.strengthItemCap) {
                count = EXPLOSIVE_VESSEL.strengthItemCap;
                break;
            }
        }

        final double multiplier = EXPLOSIVE_VESSEL.explosionMultiplier;
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
        return new VesselHandler(nbt, type);
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