package mod.traister101.sns.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.*;

import lombok.*;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public sealed class SimpleCapabilityProvider<Handler> implements ICapabilityProvider {

	protected final Lazy<Handler> handler;
	private final Capability<? super Handler> capability;

	public static <Handler> SimpleCapabilityProvider<Handler> of(final Lazy<Handler> handler, final Capability<? super Handler> capability) {
		return new SimpleCapabilityProvider<>(handler, capability);
	}

	public static <Handler extends INBTSerializable<CompoundTag>> SimpleSerializedCapabilityProvider<Handler> serialized(
			final Lazy<Handler> handler, final Capability<? super Handler> capability) {
		return new SimpleSerializedCapabilityProvider<>(handler, capability);
	}

	@Override
	public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
		if (capability == cap) {
			return LazyOptional.of(handler::get).cast();
		}
		return LazyOptional.empty();
	}

	public static final class SimpleSerializedCapabilityProvider<Handler extends INBTSerializable<CompoundTag>> extends
			SimpleCapabilityProvider<Handler> implements INBTSerializable<CompoundTag> {

		private SimpleSerializedCapabilityProvider(final Lazy<Handler> handler, final Capability<? super Handler> capability) {
			super(handler, capability);
		}

		@Override
		public CompoundTag serializeNBT() {
			return handler.get().serializeNBT();
		}

		@Override
		public void deserializeNBT(final CompoundTag nbt) {
			handler.get().deserializeNBT(nbt);
		}
	}
}