package mod.traister101.sns.datagen;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.datagen.providers.*;
import mod.traister101.sns.datagen.providers.tags.*;

import net.minecraft.DetectedVersion;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import lombok.experimental.UtilityClass;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
@Mod.EventBusSubscriber(modid = SacksNSuch.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

	@SubscribeEvent
	public static void gatherData(final GatherDataEvent event) {
		final var generator = event.getGenerator();
		final var lookupProvider = event.getLookupProvider();
		final var existingFileHelper = event.getExistingFileHelper();
		final var packOutput = generator.getPackOutput();

		generator.addProvider(true, new PackMetadataGenerator(packOutput).add(PackMetadataSection.TYPE,
				new PackMetadataSection(Component.translatable(BuiltInLanguage.PACK_DESCRIPTION),
						DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
						Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion)))));

		final var blockTags = generator.addProvider(event.includeServer(), new BuiltInBlockTags(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new BuiltInItemTags(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
		generator.addProvider(event.includeServer(), new BuiltInEntityTags(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new BuiltInRecipes(packOutput));
		generator.addProvider(event.includeServer(), new BuiltInCurios(packOutput, existingFileHelper, lookupProvider));
		generator.addProvider(event.includeServer(), new BuiltInItemSizes(packOutput));
		generator.addProvider(event.includeServer(), new BuiltInItemHeats(packOutput));
		final var advancementProvider = BuiltInAvdancements.create(packOutput, lookupProvider, existingFileHelper);
		generator.addProvider(event.includeServer(), advancementProvider);

		generator.addProvider(event.includeClient(), new BuiltInLanguage(packOutput, advancementProvider));
		generator.addProvider(event.includeClient(), new BuiltInItemModels(packOutput, existingFileHelper));
		generator.addProvider(event.includeClient(), new BuiltInSpriteSources(packOutput, existingFileHelper));
	}
}