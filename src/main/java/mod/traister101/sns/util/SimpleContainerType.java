package mod.traister101.sns.util;

import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.config.entries.ContainerConfig;
import net.dries007.tfc.common.capabilities.size.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.items.IItemHandler;

import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.*;
import org.jetbrains.annotations.*;
import java.util.Optional;
import java.util.function.*;

@Builder
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SimpleContainerType implements ContainerType {

	Supplier<Integer> slotCount;
	Supplier<Integer> slotCapacity;
	Supplier<Boolean> doPickup;
	Supplier<Boolean> doVoiding;
	Supplier<Boolean> doInventoryInteraction;
	Supplier<Size> allowedSize;
	/**
	 * @see ConstantSize
	 */
	@Default
	Function<ItemStack, Size> sizeFunction = ConstantSize.NORMAL;
	/**
	 * @see ConstantWeight
	 */
	@Default
	Function<ItemStack, Weight> weightFunction = ConstantWeight.VERY_HEAVY;
	@Getter
	@Default
	TagKey<Item> preventedItems = SNSItemTags.PREVENTED_IN_ITEM_CONTAINERS;
	@Nullable TagKey<Item> allowedItems;
	@Default
	CapabilityFactory capabilityFactory = (type, owner, nbt) -> standardItemContainer(type, owner, ContainerItemHandler::new);

	@Contract(" -> new")
	public static SimpleContainerTypeBuilder builder() {
		return new SimpleContainerTypeBuilder();
	}

	@Contract("_ -> new")
	public static SimpleContainerTypeBuilder builder(final ContainerConfig config) {
		return new SimpleContainerTypeBuilder().slotCount(config.slotCount)
				.slotCapacity(config.slotCap)
				.doPickup(config.doPickup)
				.doVoiding(config.doVoiding)
				.doInventoryInteraction(config.doInventoryTransfer)
				.allowedSize(config.allowedSize);
	}

	/**
	 * @param type The container type
	 * @param owner The owner stack
	 * @param handlerFactory The item handler factory
	 */
	public static <H extends IItemHandler & INBTSerializable<CompoundTag>> ICapabilityProvider standardItemContainer(final ContainerType type,
			final ItemStack owner, final BiFunction<ContainerType, ItemStack, ? extends H> handlerFactory) {
		final var builder = CompactCapabilityDispatcher.builder();

		builder.simpleSerializedCap(Lazy.of(() -> handlerFactory.apply(type, owner)), ForgeCapabilities.ITEM_HANDLER);
		builder.simpleCap(Lazy.of(() -> new SimpleDynamicCachedWeight(owner, SimpleDynamicCachedWeight::percentageWeight)),
				SNSCapabilities.DYNAMIC_WEIGHT);
		builder.simpleSerializedCap(Lazy.of(SimpleItemVoider::new), SNSCapabilities.ITEM_VOIDER);

		return builder.build();
	}

	@Override
	public int slotCount() {
		return slotCount.get();
	}

	@Override
	public int slotCapacity() {
		return slotCapacity.get();
	}

	@Override
	public boolean doesAutoPickup() {
		return doPickup.get();
	}

	@Override
	public boolean doesVoiding() {
		return doVoiding.get();
	}

	@Override
	public boolean doesInventoryInteraction() {
		return doInventoryInteraction.get();
	}

	@Override
	public Size allowedSize() {
		return allowedSize.get();
	}

	@Override
	public Size size(final ItemStack itemStack) {
		return sizeFunction.apply(itemStack);
	}

	@Override
	public Weight weight(final ItemStack itemStack) {
		return weightFunction.apply(itemStack);
	}

	@Override
	public Optional<TagKey<Item>> allowedItems() {
		return Optional.ofNullable(allowedItems);
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack itemStack, final @Nullable CompoundTag nbt) {
		return capabilityFactory.create(this, itemStack, nbt);
	}

	public enum ConstantSize implements Function<ItemStack, Size> {
		TINY,
		VERY_SMALL,
		SMALL,
		NORMAL,
		LARGE,
		VERY_LARGE,
		HUGE;

		@Override
		public Size apply(final ItemStack itemStack) {
			return switch (this) {
				case TINY -> Size.TINY;
				case VERY_SMALL -> Size.VERY_SMALL;
				case SMALL -> Size.SMALL;
				case NORMAL -> Size.NORMAL;
				case LARGE -> Size.LARGE;
				case VERY_LARGE -> Size.VERY_LARGE;
				case HUGE -> Size.HUGE;
			};
		}
	}

	public enum ConstantWeight implements Function<ItemStack, Weight> {
		VERY_LIGHT,
		LIGHT,
		MEDIUM,
		HEAVY,
		VERY_HEAVY;

		@Override
		public Weight apply(final ItemStack itemStack) {
			return switch (this) {
				case VERY_LIGHT -> Weight.VERY_LIGHT;
				case LIGHT -> Weight.LIGHT;
				case MEDIUM -> Weight.MEDIUM;
				case HEAVY -> Weight.HEAVY;
				case VERY_HEAVY -> Weight.VERY_HEAVY;
			};
		}
	}

	@FunctionalInterface
	public interface CapabilityFactory {

		/**
		 * @param type The container type the factory applies to
		 * @param owner The owning stack
		 * @param tag NBT of this item serialized, or null.
		 */
		ICapabilityProvider create(ContainerType type, ItemStack owner, @Nullable CompoundTag tag);
	}
}