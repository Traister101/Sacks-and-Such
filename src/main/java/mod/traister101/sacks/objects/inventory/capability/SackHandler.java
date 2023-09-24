package mod.traister101.sacks.objects.inventory.capability;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.api.types.SackType;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils;
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

import javax.annotation.Nullable;

// TODO needs more touch up
public class SackHandler extends ExtendedSlotCapacityHandler {

	private final SackType type;

	public SackHandler(final @Nullable NBTTagCompound nbt, final SackType type) {
		super(nbt, type.getSlotCount(), type.getSlotCapacity());
		this.type = type;
	}

	@Override
	public int getStackLimit(final int slotIndex, final ItemStack itemStack) {
		if (type == SackType.KNAPSACK) return itemStack.getMaxStackSize();
		return super.getStackLimit(slotIndex, itemStack);
	}

	// TODO more ore config
	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		final Item item = itemStack.getItem();

		// Stack is a sack, no sack-ception
		if (item instanceof ItemSack) return false;

		if (type == SackType.FARMER_SACK) {
			// Allow other than seeds
			if (!ConfigSNS.SACK.FARMER_SACK.allowNonSeed) {
				if (!(item instanceof ItemSeedsTFC)) return false;
			}
		}

		// Food in every sack
		if (!ConfigSNS.GLOBAL.allAllowFood)
			if (item instanceof ItemFoodTFC) return false;

		if (type == SackType.MINER_SACK) {
			//Allow other than ore
			if (!ConfigSNS.SACK.MINER_SACK.allowNonOre) {
				//If item is a TFC ore
				if (!(item instanceof ItemOreTFC || item instanceof ItemSmallOre)) return false;
			}
		}

		// Ore for all sacks
		if (!ConfigSNS.GLOBAL.allAllowOre)
			if (type != SackType.MINER_SACK)
				if (item instanceof ItemOreTFC || item instanceof ItemSmallOre) return false;

		final IItemSize stackSize = CapabilityItemSize.getIItemSize(itemStack);
		if (stackSize != null) {
			final Size size = stackSize.getSize(itemStack);
			// Larger than the sacks slot size
			//noinspection RedundantIfStatement
			if (!SNSUtils.isSmallerOrEqualTo(size, type.getAllowedSize())) return false;
		}

		return true;
	}

	// TODO rethink if this is needed
	public boolean hasItems() {
		return stacks.stream().anyMatch(itemStack -> !itemStack.isEmpty());
	}
}