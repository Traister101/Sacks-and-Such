package mod.traister101.sns.datagen.tfc.data;

import com.google.gson.JsonObject;

import net.minecraft.data.*;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class SimpleDataProvider implements DataProvider {

	private final PathProvider pathProvider;

	private final Map<ResourceLocation, JsonObject> data = new HashMap<>();

	protected SimpleDataProvider(final PathProvider pathProvider) {
		this.pathProvider = pathProvider;
	}

	protected abstract void addData();

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		addData();

		if (!data.isEmpty()) {
			final List<CompletableFuture<?>> futures = new ArrayList<>();

			for (final var entry : data.entrySet()) {
				final var key = entry.getKey();
				final var value = entry.getValue();

				futures.add(DataProvider.saveStable(cache, value, pathProvider.json(key)));
			}

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[] {}));
		}

		return CompletableFuture.allOf();
	}

	protected final void add(final ResourceLocation location, final JsonObject jsonObject) {
		data.put(location, jsonObject);
	}
}