package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.datagen.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;

import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class BuiltInAvdancements {

	public static SmarterAdvancementProvider create(final PackOutput packOutput, final CompletableFuture<Provider> registries,
			final ExistingFileHelper existingFileHelper) {
		return new SmarterAdvancementProvider(packOutput, registries, existingFileHelper, List.of(new TFCStoryAdvacementGenerator()));
	}
}