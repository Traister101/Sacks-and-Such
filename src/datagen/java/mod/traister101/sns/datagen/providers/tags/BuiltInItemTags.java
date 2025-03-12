package mod.traister101.sns.datagen.providers.tags;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.items.SNSItems;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.*;
import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class BuiltInItemTags extends ItemTagsProvider {

	public BuiltInItemTags(final PackOutput packOutput, final CompletableFuture<Provider> lookupProvider,
			final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, blockTags, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(final Provider provider) {
		tag(SNSItemTags.PREVENTED_IN_ITEM_CONTAINERS).add(SNSItems.STRAW_BASKET.get(), SNSItems.LEATHER_SACK.get(), SNSItems.BURLAP_SACK.get(),
				SNSItems.ORE_SACK.get(), SNSItems.SEED_POUCH.get(), SNSItems.FRAME_PACK.get(), SNSItems.LUNCHBOX.get(), SNSItems.QUIVER.get());
		tag(SNSItemTags.ALLOWED_IN_SEED_POUCH).addTag(SNSItemTags.TFC_SEEDS);
		tag(SNSItemTags.ALLOWED_IN_ORE_SACK).addTag(SNSItemTags.TFC_SMALL_ORE_PIECES).addTag(SNSItemTags.TFC_ORE_PIECES);
		tag(SNSItemTags.LUNCHBOX_FOOD).add(TFCItems.FOOD.get(Food.BARLEY_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.BARLEY_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.MAIZE_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.MAIZE_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.OAT_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.OAT_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.RYE_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.RYE_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.RICE_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.RICE_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.WHEAT_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.WHEAT_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.COOKED_EGG).get(),
						TFCItems.FOOD.get(Food.BOILED_EGG).get())
				.addTag(SNSItemTags.TFC_SOUPS)
				.addTag(SNSItemTags.TFC_SALADS)
				.addTag(SNSItemTags.TFC_BREADS)
				.addTag(SNSItemTags.TFC_COOKED_MEATS)
				.addTag(SNSItemTags.TFC_DAIRY)
				.addTag(SNSItemTags.TFC_FIRUITS)
				.addTag(SNSItemTags.TFC_VEGETABLES);
		tag(SNSItemTags.ALLOWED_IN_QUIVER).addTag(ItemTags.ARROWS).addTag(SNSItemTags.TFC_JAVELINS);

		tag(TFCTags.Items.USABLE_ON_TOOL_RACK).add(SNSItems.STRAW_BASKET.get(), SNSItems.LEATHER_SACK.get(), SNSItems.BURLAP_SACK.get(),
				SNSItems.ORE_SACK.get(), SNSItems.SEED_POUCH.get(), SNSItems.FRAME_PACK.get(), SNSItems.LUNCHBOX.get(), SNSItems.QUIVER.get());

		// Curios
		tag(TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, "belt"))).add(SNSItems.LEATHER_SACK.get(),
				SNSItems.BURLAP_SACK.get(), SNSItems.ORE_SACK.get(), SNSItems.SEED_POUCH.get());
		tag(TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, "back"))).add(SNSItems.FRAME_PACK.get(), SNSItems.QUIVER.get());
	}
}