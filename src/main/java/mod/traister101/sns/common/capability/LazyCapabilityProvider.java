package mod.traister101.sns.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.items.IItemHandler;

import lombok.*;
import org.jetbrains.annotations.Nullable;

/**
 * A class to lazily initialize a handler like {@link IItemHandler} on first request.
 *
 * @see LazySerializedCapabilityProvider if your handler needs serializing
 */
@RequiredArgsConstructor
public sealed class LazyCapabilityProvider<Handler> implements ICapabilityProvider {

	private final HandlerFactory<Handler> handlerFactory;
	private final Capability<? super Handler>[] capabilities;
	@Getter(value = AccessLevel.PROTECTED, lazy = true)
	private final Handler handler = handlerFactory.create();
	private final LazyOptional<Handler> holder = LazyOptional.of(this::getHandler);

	/**
	 * @param handlerFactory The handler factory
	 * @param capabilities The capabilities this handler possesses
	 *
	 * @see #ofSerialized(HandlerFactory, Capability[])
	 */
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <H> LazyCapabilityProvider<H> of(final HandlerFactory<H> handlerFactory, final Capability<? super H>... capabilities) {
		if (capabilities.length < 1) throw new IllegalArgumentException("Handler must support at least one capability");
		return new LazyCapabilityProvider<>(handlerFactory, capabilities);
	}

	/**
	 * @param handlerFactory The handler factory
	 * @param capabilities The capabilities this handler possesses
	 */
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <H extends INBTSerializable<CompoundTag>> LazySerializedCapabilityProvider<H> ofSerialized(final HandlerFactory<H> handlerFactory,
			final Capability<? super H>... capabilities) {
		if (capabilities.length < 1) throw new IllegalArgumentException("Handler must support at least one capability");
		return new LazySerializedCapabilityProvider<>(handlerFactory, capabilities);
	}

	@Override
	public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
		for (final var capability : capabilities) {
			if (capability == cap) {
				return holder.cast();
			}
		}

		return LazyOptional.empty();
	}

	@FunctionalInterface
	public interface HandlerFactory<Handler> {

		/**
		 * Create the handler object
		 */
		Handler create();
	}

	public static final class LazySerializedCapabilityProvider<Handler extends INBTSerializable<CompoundTag>> extends
			LazyCapabilityProvider<Handler> implements INBTSerializable<CompoundTag> {

		public LazySerializedCapabilityProvider(final HandlerFactory<Handler> handlerFactory, final Capability<? super Handler>[] capabilities) {
			super(handlerFactory, capabilities);
		}

		@Override
		public CompoundTag serializeNBT() {
			return getHandler().serializeNBT();
		}

		@Override
		public void deserializeNBT(final CompoundTag compoundTag) {
			getHandler().deserializeNBT(compoundTag);
		}
	}
}