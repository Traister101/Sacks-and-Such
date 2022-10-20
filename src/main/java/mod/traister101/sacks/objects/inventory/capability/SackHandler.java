package mod.traister101.sacks.objects.inventory.capability;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SackHandler extends AbstractHandler {

    private final SackType type;

    public SackHandler(@Nullable NBTTagCompound nbt, @Nonnull SackType type) {
        super(nbt, type.slots, type.stackCap);
        this.type = type;
    }

    // TODO More sacks (like the food sack) will need to be handled
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.isStackable()) return false;
        // Stack is a sack, no sack-ception
        if (stack.getItem() instanceof ItemSack) return false;

        Item item = stack.getItem();

        if (type == SackType.FARMER) {
            // Allow other than seeds
            if (!ConfigSNS.FARMER_SACK.allowNonSeed) {
                if (!(item instanceof ItemSeedsTFC)) return false;
            }
        }

        if (!ConfigSNS.GLOBAL.allAllowFood)
            if (item instanceof ItemFoodTFC) return false;

        if (type == SackType.MINER) {
            //Allow other than ore
            if (!ConfigSNS.MINER_SACK.allowNonOre) {
                //If item is a TFC ore
                if (!(item instanceof ItemOreTFC || item instanceof ItemSmallOre)) return false;
            }
        }

        if (!ConfigSNS.GLOBAL.allAllowOre)
            if (type != SackType.MINER)
                if (item instanceof ItemOreTFC || item instanceof ItemSmallOre) return false;

        final IItemSize stackSize = CapabilityItemSize.getIItemSize(stack);
        if (stackSize != null) {
            final Size size = stackSize.getSize(stack);
            // Larger than the sacks slot size
            if (!SNSUtils.isSmallerOrEqualTo(size, type.allowedSize)) return false;
        }

        ItemStack currentStack = getStackInSlot(slot);
        setStackInSlot(slot, ItemStack.EMPTY);
        ItemStack remainder = insertItem(slot, stack, true);
        setStackInSlot(slot, currentStack);

        return remainder.isEmpty() || remainder.getCount() < stack.getCount();
    }
}