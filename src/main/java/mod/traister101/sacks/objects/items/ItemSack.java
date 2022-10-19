package mod.traister101.sacks.objects.items;

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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSack extends Item implements IItemSize {

    private final SackType type;
    private Weight weight;
    private Size size;

    public ItemSack(@Nonnull SackType type) {
        setWeight(Weight.MEDIUM);
        setSize(Size.NORMAL);
        setMaxStackSize(1);
        this.type = type;
    }

    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack heldStack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {

            if (playerIn.isSneaking()) {
                if (ConfigSNS.GLOBAL.shiftClickTogglesVoid) {
                    SNSUtils.sendPacketAndStatus(SNSUtils.isAutoVoid(heldStack), ToggleType.VOID);
                } else {
                    SNSUtils.sendPacketAndStatus(SNSUtils.isAutoPickup(heldStack), ToggleType.PICKUP);
                }
            } else GuiHandler.openGui(worldIn, playerIn, type.gui);
        }
        return new ActionResult<>(EnumActionResult.FAIL, heldStack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String text = SacksNSuch.MODID + ".sack.tooltip";
        if (GuiScreen.isShiftKeyDown()) {
            if (SNSUtils.isAutoVoid(stack)) {
                text += ".void";
            }
            if (SNSUtils.isAutoPickup(stack)) {
                text += ".pickup";
            }
            text += ".shift";
        }
        tooltip.add(I18n.format(text));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        if (ConfigSNS.GLOBAL.voidGlint) return SNSUtils.isAutoVoid(stack);
        return SNSUtils.isAutoPickup(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new SackHandler(nbt, type);
    }

    @Nonnull
    public SackType getType() {
        return type;
    }

    public ItemSack setSize(@Nonnull Size size) {
        this.size = size;
        return this;
    }

    public ItemSack setWeight(@Nonnull Weight weight) {
        this.weight = weight;
        return this;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack) {
        return size;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack) {
        return weight;
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack) {
        return false;
    }
}