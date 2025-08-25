package mod.traister101.sns.common.capability;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.*;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import java.util.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompactCapabilityDispatcher implements ICapabilityProvider, INBTSerializable<CompoundTag> {

	private ICapabilityProvider[] capabilities;
	private INBTSerializable<CompoundTag>[] serializables;

	@Builder
	@SuppressWarnings("unchecked")
	private static CompactCapabilityDispatcher of(final @Singular List<? extends ICapabilityProvider> capabilities,
			final @Singular List<? extends INBTSerializable<CompoundTag>> serializables) {
		return new CompactCapabilityDispatcher(capabilities.toArray(ICapabilityProvider[]::new), serializables.toArray(INBTSerializable[]::new));
	}

	@Override
	public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
		for (final var provider : capabilities) {
			final var capability = provider.getCapability(cap, side);
			if (capability.isPresent()) return capability;
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag compoundTag = new CompoundTag();

		for (final var serializable : serializables) {
			compoundTag.merge(serializable.serializeNBT());
		}

		return compoundTag;
	}

	@Override
	public void deserializeNBT(final CompoundTag compoundTag) {
		Arrays.stream(serializables).forEach(serializable -> serializable.deserializeNBT(compoundTag));
	}

	public static class CompactCapabilityDispatcherBuilder {

		@CanIgnoreReturnValue
		public <T extends ICapabilityProvider & INBTSerializable<CompoundTag>> CompactCapabilityDispatcherBuilder serializedCap(
				final T serializableProvider) {
			return capability(serializableProvider).serializable(serializableProvider);
		}

		@CanIgnoreReturnValue
		public <H> CompactCapabilityDispatcherBuilder simpleCap(final Lazy<H> handler, final Capability<? super H> capability) {
			return capability(SimpleCapabilityProvider.of(handler, capability));
		}

		@CanIgnoreReturnValue
		public <H extends INBTSerializable<CompoundTag>> CompactCapabilityDispatcherBuilder simpleSerializedCap(final Lazy<H> handler,
				final Capability<? super H> capability) {
			final var provider = SimpleCapabilityProvider.serialized(handler, capability);
			return capability(provider).serializable(provider);
		}
	}
}