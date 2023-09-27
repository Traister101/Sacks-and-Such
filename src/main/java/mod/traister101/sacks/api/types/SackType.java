package mod.traister101.sacks.api.types;

import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.NBTHelper;
import net.dries007.tfc.api.capability.size.Size;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static mod.traister101.sacks.SacksNSuch.MODID;
import static net.dries007.tfc.util.Helpers.getNull;

/**
 * A type of sack, used to cleanly sync server config to the client, so we can safely have dynamic size containers controlled via config.
 *
 * To register your own SackTypes subscribe to the {@link net.dries007.tfc.api.registries.TFCRegistryEvent.RegisterPreBlock} event as these are
 * also used to automatically register sack items.
 *
 * If you extend the sack type make sure you override {@link SackType#serializeNBT()} and {@link SackType#deserializeNBT(NBTTagCompound)} for any
 * new fields that need to be synced to the client.
 */
@ObjectHolder(MODID)
public class SackType extends IForgeRegistryEntry.Impl<SackType> implements INBTSerializable<NBTTagCompound> {

	public static final SackType THATCH_SACK = getNull();
	public static final SackType LEATHER_SACK = getNull();
	public static final SackType BURLAP_SACK = getNull();
	public static final SackType MINER_SACK = getNull();
	public static final SackType FARMER_SACK = getNull();
	public static final SackType KNAPSACK = getNull();
	private int slotCount;
	private int slotCapacity;
	private boolean doesAutoPickup;
	private boolean doesVoiding;
	private Size allowedSize;

	/**
	 * @param slotCount The amount of slots the sack should have
	 * @param slotCapacity The capacity of each slot
	 * @param doAutoPickup If this sack should have automatic pickup capabilities
	 * @param doesVoiding If this sack should have voiding capabilities
	 * @param allowedSize The maximum {@link Size} allowed inside the sack
	 */
	public SackType(final int slotCount, final int slotCapacity, final boolean doAutoPickup, final boolean doesVoiding, final Size allowedSize) {
		this.slotCount = slotCount;
		this.slotCapacity = slotCapacity;
		this.doesAutoPickup = doAutoPickup;
		this.doesVoiding = doesVoiding;
		this.allowedSize = allowedSize;
	}

	public int getSlotCount() {
		return slotCount;
	}

	public int getSlotCapacity() {
		return slotCapacity;
	}

	public boolean doesAutoPickup() {
		return doesAutoPickup;
	}

	public boolean doesVoiding() {
		return doesVoiding;
	}

	public Size getAllowedSize() {
		return allowedSize;
	}

	/**
	 * Abstracted out to allow custom overrides without the need of a new item class.
	 * Called by {@link ItemSack#getSize(ItemStack)}
	 *
	 * @param itemStack The ItemStack instance
	 *
	 * @return Size for the stack
	 */
	public Size getSize(final ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemSack) {
			if (NBTHelper.doesSackHaveItems(itemStack)) {
				if (this == KNAPSACK) return Size.HUGE;
				return Size.LARGE;
			}
		}
		return Size.NORMAL;
	}

	@Override
	public String toString() {
		//noinspection DataFlowIssue
		return getRegistryName().toString();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("SlotCount", slotCount);
		compound.setInteger("SlotCapacity", slotCapacity);
		compound.setBoolean("DoesAutoPickup", doesAutoPickup);
		compound.setBoolean("DoesVoiding", doesVoiding);
		compound.setInteger("AllowedSize", allowedSize.ordinal());
		return compound;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound compound) {
		slotCount = compound.getInteger("SlotCount");
		slotCapacity = compound.getInteger("SlotCapacity");
		doesAutoPickup = compound.getBoolean("DoesAutoPickup");
		doesVoiding = compound.getBoolean("DoesVoiding");
		allowedSize = Size.values()[compound.getInteger("AllowedSize")];
	}
}