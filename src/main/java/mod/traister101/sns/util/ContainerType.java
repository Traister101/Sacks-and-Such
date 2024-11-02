package mod.traister101.sns.util;

import mod.traister101.sns.common.capability.ContainerItemHandler;
import mod.traister101.sns.common.capability.LazyCapabilityProvider.LazySerializedCapabilityProvider;
import mod.traister101.sns.common.items.ContainerItem;
import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.*;

import org.jetbrains.annotations.Nullable;

public interface ContainerType extends StringRepresentable {

	/**
	 * @return If the passed {@link ItemStack} supports auto pickup
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static boolean canDoItemPickup(final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ContainerItem containerItem)) return false;

		if (!containerItem.type.doesAutoPickup()) return false;

		return NBTHelper.isAutoPickup(itemStack);
	}

	/**
	 * @return If the passed {@link ItemStack} supports voiding
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static boolean canDoItemVoiding(final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ContainerItem containerItem)) return false;

		return containerItem.type.doesVoiding();
	}

	/**
	 * @return The amount of slots this {@link ContainerType}
	 */
	int getSlotCount();

	/**
	 * @return The slot capacity of this {@link ContainerType}
	 */
	int getSlotCapacity();

	/**
	 * @return If this {@link ContainerType} supports item pickup
	 */
	boolean doesAutoPickup();

	/**
	 * @return If this {@link ContainerType} supports item voiding
	 */
	boolean doesVoiding();

	/**
	 * @return If this {@link ContainerType} allows you to tranasfer items via the inventory
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	boolean doesInventoryInteraction();

	/**
	 * @return The largest allowed {@link Size} inside the sack
	 */
	Size getAllowedSize();

	/**
	 * Abstracted out to allow custom overrides without the need of a new item class. Called by {@link ContainerItem#getSize(ItemStack)}
	 *
	 * @param itemStack The {@link ContainerItem} instance
	 *
	 * @return Size for the stack
	 */
	Size getSize(final ItemStack itemStack);

	/**
	 * @param itemStack The {@link ItemStack}
	 * @param nbt The {@link CompoundTag}
	 *
	 * @return The {@link ICapabilityProvider} for the {@link ContainerItem}s of this {@link ContainerType}
	 */
	default ICapabilityProvider getCapabilityProvider(final ItemStack itemStack, final @Nullable CompoundTag nbt) {
		return new LazySerializedCapabilityProvider<>(() -> new ContainerItemHandler(this, itemStack), ForgeCapabilities.ITEM_HANDLER);
	}
}