package mod.traister101.sns.datagen;

import mod.traister101.sns.datagen.SmartLanguageProvider.ExtraLanguageProvider;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.*;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class SmarterAdvancementProvider extends AdvancementProvider implements ExtraLanguageProvider {

	private final List<SmarterAdvancementGenerator> generators;

	public SmarterAdvancementProvider(final PackOutput output, final CompletableFuture<Provider> registries,
			final ExistingFileHelper existingFileHelper, final List<SmarterAdvancementGenerator> generators) {
		super(output, registries, generators.stream().map(generator -> generator.toSubProvider(existingFileHelper)).toList());
		this.generators = generators;
	}

	@Override
	public void addTranslations(final TranslationWriter writer) {
		generators.forEach(generator -> generator.addTranslations(writer));
	}

	/**
	 * An interface used to generated modded advancements. This is parallel to
	 * vanilla's {@link AdvancementSubProvider} with access to the {@link ExistingFileHelper}.
	 *
	 * @see AdvancementSubProvider
	 */
	public interface SmarterAdvancementGenerator extends ExtraLanguageProvider {

		/**
		 * A method used to generate advancements for a mod. Advancements should be
		 * built via {@link net.minecraftforge.common.extensions.IForgeAdvancementBuilder#save(Consumer, ResourceLocation, ExistingFileHelper)}.
		 *
		 * @param registries a lookup for registries and their objects
		 * @param saver a consumer used to write advancements to a file
		 * @param existingFileHelper a helper used to find whether a file exists
		 */
		void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper);

		/**
		 * Creates an {@link AdvancementSubProvider} from this generator.
		 *
		 * @param existingFileHelper a helper used to find whether a file exists
		 *
		 * @return a sub provider wrapping this generator
		 */
		default AdvancementSubProvider toSubProvider(ExistingFileHelper existingFileHelper) {
			return (registries, saver) -> generate(registries, saver, existingFileHelper);
		}

		record AdvancementComponent(String title, String description) {

		}
	}
}