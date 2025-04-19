package mod.traister101.sns.common.items;

import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.items.LunchBoxItem.LunchboxHandler;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.config.entries.ContainerConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.Function;

public final class DefaultContainers {

	public static final ContainerType STRAW_BASKET = new ContainerItemType("straw_basket", Size.NORMAL, SNSConfig.SERVER.strawBasket,
			SNSItemTags.PREVENTED_IN_STRAW_BASKET, null, genericContainerProvider());
	public static final ContainerType LEATHER_SACK = new ContainerItemType("leather_sack", Size.NORMAL, SNSConfig.SERVER.leatherSack,
			SNSItemTags.PREVENTED_IN_LEATHER_SACK, null, genericContainerProvider());
	public static final ContainerType BURLAP_SACK = new ContainerItemType("burlap_sack", Size.NORMAL, SNSConfig.SERVER.burlapSack,
			SNSItemTags.PREVENTED_IN_BURLAP_SACK, null, genericContainerProvider());
	public static final ContainerType ORE_SACK = new ContainerItemType("ore_sack", Size.NORMAL, SNSConfig.SERVER.oreSack,
			SNSItemTags.PREVENTED_IN_ORE_SACK, SNSItemTags.ALLOWED_IN_ORE_SACK, genericContainerProvider());
	public static final ContainerType SEED_POUCH = new ContainerItemType("seed_pouch", Size.NORMAL, SNSConfig.SERVER.seedPouch,
			SNSItemTags.PREVENTED_IN_SEED_POUCH, SNSItemTags.ALLOWED_IN_SEED_POUCH, genericContainerProvider());
	public static final ContainerType FRAME_PACK = new ContainerItemType("frame_pack", Size.HUGE, SNSConfig.SERVER.framePack,
			SNSItemTags.PREVENTED_IN_FRAME_PACK, null, capabilityProvider(containerItemType -> new ContainerItemHandler(containerItemType) {
		@Override
		public int getStackLimit(final int slotIndex, final ItemStack itemStack) {
			return itemStack.getMaxStackSize();
		}
	}, ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER));
	public static final ContainerType LUNCHBOX = new ContainerItemType("lunchbox", Size.NORMAL, SNSConfig.SERVER.lunchBox,
			SNSItemTags.PREVENTED_IN_LUNCHBOX, SNSItemTags.LUNCHBOX_FOOD,
			capabilityProvider(LunchboxHandler::new, ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.LUNCHBOX,
					SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER));
	public static final ContainerType QUIVER = new ContainerItemType("quiver", Size.HUGE, SNSConfig.SERVER.quiver, SNSItemTags.PREVENTED_IN_QUIVER,
			SNSItemTags.ALLOWED_IN_QUIVER, genericContainerProvider());

	@SafeVarargs
	@SuppressWarnings("varargs")
	private static <Handler extends INBTSerializable<CompoundTag>> Function<ContainerItemType, ICapabilityProvider> capabilityProvider(
			final Function<ContainerItemType, Handler> handlerFactory, final Capability<? super Handler>... capabilities) {
		return containerItemType -> LazyCapabilityProvider.ofSerialized(() -> handlerFactory.apply(containerItemType), capabilities);
	}

	private static Function<ContainerItemType, ICapabilityProvider> genericContainerProvider() {
		return capabilityProvider(ContainerItemHandler::new, ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);
	}

	@RequiredArgsConstructor
	private static final class ContainerItemType implements ContainerType {

		private final String name;
		private final Size size;
		private final ContainerConfig containerConfig;
		private final TagKey<Item> itemBlackList;
		@Nullable
		private final TagKey<Item> allowedItems;
		private final Function<ContainerItemType, ICapabilityProvider> capabilityProviderFactory;

		@Override
		public int getSlotCount() {
			return containerConfig.slotCount.get();
		}

		@Override
		public int getSlotCapacity() {
			return containerConfig.slotCap.get();
		}

		@Override
		public boolean doesAutoPickup() {
			return containerConfig.doPickup.get();
		}

		@Override
		public boolean doesVoiding() {
			return containerConfig.doVoiding.get();
		}

		@Override
		public boolean doesInventoryInteraction() {
			return containerConfig.doInventoryTransfer.get();
		}

		@Override
		public Size getAllowedSize() {
			return containerConfig.allowedSize.get();
		}

		@Override
		public Size getSize(final ItemStack itemStack) {
			return size;
		}

		@Override
		public TagKey<Item> preventedItems() {
			return this.itemBlackList;
		}

		@Override
		public Optional<TagKey<Item>> allowedItems() {
			return Optional.ofNullable(allowedItems);
		}

		@Override
		public ICapabilityProvider getCapabilityProvider(final ItemStack itemStack, @Nullable final CompoundTag nbt) {
			return capabilityProviderFactory.apply(this);
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}
}