package mod.traister101.sns.common.items;

import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.capability.LazyCapabilityProvider.LazySerializedCapabilityProvider;
import mod.traister101.sns.common.items.LunchBoxItem.LunchboxHandler;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.config.ServerConfig.ContainerConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;

import org.jetbrains.annotations.Nullable;
import java.util.function.BiFunction;

public final class DefaultContainers {

	public static final ContainerType STRAW_BASKET = new ContainerItemType<>("straw_basket", Size.NORMAL, SNSConfig.SERVER.strawBasket,
			GenericHandler::new, ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);

	public static final ContainerType LEATHER_SACK = new ContainerItemType<>("leather_sack", Size.NORMAL, SNSConfig.SERVER.leatherSack,
			GenericHandler::new, ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);

	public static final ContainerType BURLAP_SACK = new ContainerItemType<>("burlap_sack", Size.NORMAL, SNSConfig.SERVER.burlapSack,
			GenericHandler::new, ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);

	public static final ContainerType ORE_SACK = new ContainerItemType<>("ore_sack", Size.NORMAL, SNSConfig.SERVER.oreSack, OreSackHandler::new,
			ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);

	public static final ContainerType SEED_POUCH = new ContainerItemType<>("seed_pouch", Size.NORMAL, SNSConfig.SERVER.seedPouch,
			SeedPouchHandler::new, ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);

	public static final ContainerType FRAME_PACK = new ContainerItemType<>("frame_pack", Size.HUGE, SNSConfig.SERVER.framePack, FramePackHandler::new,
			ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);

	public static final ContainerType LUNCHBOX = new ContainerItemType<>("lunchbox", Size.NORMAL, SNSConfig.SERVER.lunchBox, LunchboxHandler::new,
			ForgeCapabilities.ITEM_HANDLER, SNSCapabilities.LUNCHBOX, SNSCapabilities.ITEM_VOIDING_ITEM_HANDLER);

	private record ContainerItemType<Handler extends INBTSerializable<CompoundTag>>(String name,
			Size size,
			ContainerConfig containerConfig,
			BiFunction<ContainerType, ItemStack, Handler> handlerFactory,
			Capability<? super Handler>... capabilities) implements ContainerType {

		@SafeVarargs
		@SuppressWarnings("varargs")
		private ContainerItemType {
		}

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
		public ICapabilityProvider getCapabilityProvider(final ItemStack itemStack, @Nullable final CompoundTag nbt) {
			// Must be lazy as stacks can be created before server config is initalized
			return new LazySerializedCapabilityProvider<>(() -> handlerFactory.apply(this, itemStack), capabilities);
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	private static class GenericHandler extends ContainerItemHandler {

		public GenericHandler(final ContainerType type, final ItemStack itemStack) {
			super(type, itemStack);
		}

		@Override
		public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
			if (!SNSConfig.SERVER.allAllowFood.get() && itemStack.is(TFCTags.Items.FOODS)) return false;

			if (!SNSConfig.SERVER.allAllowOre.get() && (itemStack.is(SNSItemTags.TFC_SMALL_ORE_PIECES) || itemStack.is(SNSItemTags.TFC_ORE_PIECES)))
				return false;

			return super.isItemValid(slotIndex, itemStack);
		}
	}

	private static class SeedPouchHandler extends ContainerItemHandler {

		public SeedPouchHandler(final ContainerType type, final ItemStack itemStack) {
			super(type, itemStack);
		}

		@Override
		public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
			if (!SNSConfig.SERVER.allAllowFood.get() && itemStack.is(TFCTags.Items.FOODS)) return false;

			return itemStack.is(SNSItemTags.ALLOWED_IN_SEED_POUCH) && super.isItemValid(slotIndex, itemStack);
		}
	}

	private static class OreSackHandler extends ContainerItemHandler {

		public OreSackHandler(final ContainerType type, final ItemStack itemStack) {
			super(type, itemStack);
		}

		@Override
		public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
			if (!SNSConfig.SERVER.allAllowFood.get() && itemStack.is(TFCTags.Items.FOODS)) return false;

			return itemStack.is(SNSItemTags.ALLOWED_IN_ORE_SACK) && super.isItemValid(slotIndex, itemStack);
		}
	}

	private static class FramePackHandler extends GenericHandler {

		public FramePackHandler(final ContainerType type, final ItemStack itemStack) {
			super(type, itemStack);
		}

		@Override
		public int getStackLimit(final int slotIndex, final ItemStack itemStack) {
			return itemStack.getMaxStackSize();
		}
	}
}