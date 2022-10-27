package mod.traister101.sacks.objects.items;

import mcp.MethodsReturnNonnullByDefault;
import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.objects.inventory.capability.SackHandler;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import mod.traister101.sacks.util.SackType;
import mod.traister101.sacks.util.handlers.GuiHandler;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemSack extends Item implements IItemSize {

    private final SackType type;

    public ItemSack(SackType type) {
        setMaxStackSize(1);
        this.type = type;
    }

    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer playerIn, final EnumHand handIn) {
        final ItemStack heldStack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                if (ConfigSNS.GLOBAL.shiftClickTogglesVoid) {
                    if (type.doesVoiding) {
                        SNSUtils.sendPacketAndStatus(SNSUtils.isAutoVoid(heldStack), ToggleType.VOID);
                    } else {
                        final TextComponentTranslation status = new TextComponentTranslation(SacksNSuch.MODID + ".sack.no_void");
                        Minecraft.getMinecraft().player.sendStatusMessage(status, true);
                    }
                } else {
                    if (type.doesAutoPickup) {
                        SNSUtils.sendPacketAndStatus(SNSUtils.isAutoPickup(heldStack), ToggleType.PICKUP);
                    } else {
                        final TextComponentTranslation status = new TextComponentTranslation(SacksNSuch.MODID + ".sack.no_pickup");
                        Minecraft.getMinecraft().player.sendStatusMessage(status, true);
                    }
                }
            } else {
                GuiHandler.openGui(worldIn, playerIn, type.gui);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, heldStack);
    }

    @Override
    public void addInformation(final ItemStack stack, @Nullable final World worldIn, final List<String> tooltip, ITooltipFlag flagIn) {
        String text = SacksNSuch.MODID + ".sack.tooltip";
        if (GuiScreen.isShiftKeyDown()) {
            if (SNSUtils.isAutoVoid(stack) && type.doesVoiding) {
                text += ".void";
            }
            if (SNSUtils.isAutoPickup(stack) && type.doesAutoPickup) {
                text += ".pickup";
            }
            text += ".shift";
        }
        tooltip.add(I18n.format(text));
    }

    @Override
    public boolean hasEffect(final ItemStack stack) {
        if (ConfigSNS.GLOBAL.voidGlint) return SNSUtils.isAutoVoid(stack);
        return SNSUtils.isAutoPickup(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new SackHandler(nbt, type);
    }

    @Nonnull
    public SackType getType() {
        return type;
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack) {
        if (stack.getItem() instanceof ItemSack) {
            if (SNSUtils.doesSackHaveItems(stack)) {
                if (type == SackType.KNAPSACK) return Size.VERY_LARGE;
                return Size.LARGE;
            }
        }
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack) {
        return Weight.VERY_HEAVY;
    }

    @Override
    public boolean canStack(ItemStack stack) {
        return false;
    }
}