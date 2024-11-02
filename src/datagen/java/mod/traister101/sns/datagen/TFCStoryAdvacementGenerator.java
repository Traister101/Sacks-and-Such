package mod.traister101.sns.datagen;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.datagen.SmarterAdvancementProvider.SmarterAdvancementGenerator;
import mod.traister101.sns.util.SNSUtils;
import net.dries007.tfc.TerraFirmaCraft;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class TFCStoryAdvacementGenerator implements SmarterAdvancementGenerator {

	private static final ResourceLocation IRON_AGE = new ResourceLocation(TerraFirmaCraft.MOD_ID, "story/iron_age");
	private static final ResourceLocation STEEL_AGE = new ResourceLocation(TerraFirmaCraft.MOD_ID, "story/steel_age");
	private static final AdvancementComponent STRAW_BAASKET = advancementComponent("straw_basket");
	private static final AdvancementComponent LEATHER_SACK = advancementComponent("leather_sack");
	private static final AdvancementComponent FRAME_PACK = advancementComponent("frame_pack");
	private static final AdvancementComponent LUNCHBOX = advancementComponent("lunchbox");
	private static final AdvancementComponent HIKING_BOOTS = advancementComponent("hiking_boots");
	private static final AdvancementComponent STEEL_TOE_HIKING_BOOTS = advancementComponent("steel_toe_hiking_boots");
	private static final AdvancementComponent STEEL_HORSESHOES = advancementComponent("steel_horseshoes");

	private static AdvancementComponent advancementComponent(final String name) {
		return new AdvancementComponent(title(name), description(name));
	}

	private static String title(final String title) {
		return SacksNSuch.MODID + ".advancements." + title + ".title";
	}

	private static String description(final String description) {
		return SacksNSuch.MODID + ".advancements." + description + ".description";
	}

	@Override
	public void generate(final Provider registries, final Consumer<Advancement> saver, final ExistingFileHelper existingFileHelper) {
		Advancement.Builder.advancement()
				.parent(new ResourceLocation(TerraFirmaCraft.MOD_ID, "story/get_straw"))
				.display(SNSItems.STRAW_BASKET.get(), Component.translatable(STRAW_BAASKET.title()),
						Component.translatable(STRAW_BAASKET.description()), null, FrameType.TASK, true, true, false)
				.addCriterion("has_straw_basket", InventoryChangeTrigger.TriggerInstance.hasItems(SNSItems.STRAW_BASKET.get()))
				.requirements(new String[][] {{"has_straw_basket"}})
				.save(saver, SNSUtils.modLocation("tfc/story/straw_backet"), existingFileHelper);

		Advancement.Builder.advancement()
				.parent(new ResourceLocation(TerraFirmaCraft.MOD_ID, "story/leather"))
				.display(SNSItems.LEATHER_SACK.get(), Component.translatable(LEATHER_SACK.title()),
						Component.translatable(LEATHER_SACK.description()), null, FrameType.TASK, true, true, false)
				.addCriterion("has_leather_sack", InventoryChangeTrigger.TriggerInstance.hasItems(SNSItems.LEATHER_SACK.get()))
				.requirements(new String[][] {{"has_leather_sack"}})
				.save(saver, SNSUtils.modLocation("tfc/story/leather_sack"), existingFileHelper);

		Advancement.Builder.advancement()
				.parent(IRON_AGE)
				.display(SNSItems.LUNCHBOX.get(), Component.translatable(LUNCHBOX.title()), Component.translatable(LUNCHBOX.description()), null,
						FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_lunchbox", InventoryChangeTrigger.TriggerInstance.hasItems(SNSItems.LUNCHBOX.get()))
				.requirements(new String[][] {{"has_lunchbox"}})
				.save(saver, SNSUtils.modLocation("tfc/story/lunchbox"), existingFileHelper);

		Advancement.Builder.advancement()
				.parent(IRON_AGE)
				.display(SNSItems.HIKING_BOOTS.get(), Component.translatable(HIKING_BOOTS.title()),
						Component.translatable(HIKING_BOOTS.description()), null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_hiking_boots", InventoryChangeTrigger.TriggerInstance.hasItems(SNSItems.HIKING_BOOTS.get()))
				.requirements(new String[][] {{"has_hiking_boots"}})
				.save(saver, SNSUtils.modLocation("tfc/story/hiking_boots"), existingFileHelper);

		Advancement.Builder.advancement()
				.parent(STEEL_AGE)
				.display(SNSItems.FRAME_PACK.get(), Component.translatable(FRAME_PACK.title()), Component.translatable(FRAME_PACK.description()),
						null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_frame_pack", InventoryChangeTrigger.TriggerInstance.hasItems(SNSItems.FRAME_PACK.get()))
				.requirements(new String[][] {{"has_frame_pack"}})
				.save(saver, SNSUtils.modLocation("tfc/story/frame_pack"), existingFileHelper);

		Advancement.Builder.advancement()
				.parent(STEEL_AGE)
				.display(SNSItems.STEEL_TOE_HIKING_BOOTS.get(), Component.translatable(STEEL_TOE_HIKING_BOOTS.title()),
						Component.translatable(STEEL_TOE_HIKING_BOOTS.description()), null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_steel_toe_hiking_boots", InventoryChangeTrigger.TriggerInstance.hasItems(SNSItems.STEEL_TOE_HIKING_BOOTS.get()))
				.requirements(new String[][] {{"has_steel_toe_hiking_boots"}})
				.save(saver, SNSUtils.modLocation("tfc/story/steel_toe_hiking_boots"), existingFileHelper);

		Advancement.Builder.advancement()
				.parent(STEEL_AGE)
				.display(SNSItems.STEEL_HORSESHOES.get(), Component.translatable(STEEL_HORSESHOES.title()),
						Component.translatable(STEEL_HORSESHOES.description()), null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_steel_horseshoes", InventoryChangeTrigger.TriggerInstance.hasItems(SNSItems.STEEL_HORSESHOES.get()))
				.requirements(new String[][] {{"has_steel_horseshoes"}})
				.save(saver, SNSUtils.modLocation("tfc/story/steel_horseshoes"), existingFileHelper);
	}

	@Override
	public void addTranslations(final TranslationWriter writer) {
		writer.add(STRAW_BAASKET.title(), "Straw Basket");
		writer.add(STRAW_BAASKET.description(), "Make a primative basket out of straw");
		writer.add(LEATHER_SACK.title(), "Leather Sack");
		writer.add(LEATHER_SACK.description(), "Make a Leather Sack out of the finest of leather");
		writer.add(FRAME_PACK.title(), "Un-Sophisticated Backpack");
		writer.add(FRAME_PACK.description(), "Use steel to create a Frame Pack. How Sophisticated?");
		writer.add(LUNCHBOX.title(), "Food on the go");
		writer.add(LUNCHBOX.description(), "Forge a Lunchbox");

		writer.add(HIKING_BOOTS.title(), "These boots were made for walking");
		writer.add(HIKING_BOOTS.description(), "Make some high quality boots helping you explore on foot");
		writer.add(STEEL_TOE_HIKING_BOOTS.title(), "Protection for those toesies");
		writer.add(STEEL_TOE_HIKING_BOOTS.description(), "Protect your toes with steel");
		writer.add(STEEL_HORSESHOES.title(), "Well cared for");
		writer.add(STEEL_HORSESHOES.description(), "Make horseshoes out of steel");
	}
}