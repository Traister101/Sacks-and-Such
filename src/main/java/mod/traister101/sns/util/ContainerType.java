package mod.traister101.sns.util;

import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.items.ContainerItem;
import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;

import net.minecraftforge.common.capabilities.*;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;

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
	 * @return If this {@link ContainerType} allows you to transfer items via the inventory
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
	 * An items absence in this tag doesn't mean it can always be inserted, for example it's size could be too large
	 *
	 * @return The tag for which items are blacklisted from being inserted.
	 *
	 * @apiNote If an item is present in both this and {@link #allowedItems()} this tag takes priority
	 */
	TagKey<Item> preventedItems();

	/**
	 * An optional tag which when present acts as a whitelist
	 *
	 * @return The tag for which items are allowed in the container.
	 *
	 * @apiNote If an item is present in both {@link #preventedItems()} and this the {@link #preventedItems()} tag takes priority
	 */
	Optional<TagKey<Item>> allowedItems();

	/**
	 * @param itemStack The {@link ItemStack}
	 * @param nbt The {@link CompoundTag}
	 *
	 * @return The {@link ICapabilityProvider} for the {@link ContainerItem}s of this {@link ContainerType}
	 */
	default ICapabilityProvider getCapabilityProvider(final ItemStack itemStack, final @Nullable CompoundTag nbt) {
		return LazyCapabilityProvider.of(() -> new ContainerItemHandler(this), ForgeCapabilities.ITEM_HANDLER);
	}
}