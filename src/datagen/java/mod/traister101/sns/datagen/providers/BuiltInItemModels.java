package mod.traister101.sns.datagen.providers;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.SNSItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public class BuiltInItemModels extends ItemModelProvider {

	public static final ResourceLocation SMALL_SACK = new ResourceLocation(SacksNSuch.MODID, "item/held/small_sack");
	public static final ResourceLocation LARGE_SACK = new ResourceLocation(SacksNSuch.MODID, "item/held/large_sack");

	public BuiltInItemModels(final PackOutput output, final ExistingFileHelper existingFileHelper) {
		super(output, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		basicItem(SNSItems.UNFINISHED_LEATHER_SACK);
		basicItem(SNSItems.REINFORCED_FIBER);
		basicItem(SNSItems.REINFORCED_FABRIC);
		basicItem(SNSItems.PACK_FRAME);
		basicItem(SNSItems.LEATHER_STRIP);
		basicItem(SNSItems.BOUND_LEATHER_STRIP);
		basicItem(SNSItems.LEATHER_STRIP);
		basicItem(SNSItems.BOUND_LEATHER_STRIP);
		basicItem(SNSItems.BUCKLE);
		basicItem(SNSItems.HIKING_BOOTS);
		basicItem(SNSItems.STEEL_TOE_HIKING_BOOTS, new ResourceLocation(SacksNSuch.MODID, "item/hiking_boots"));
		basicItem(SNSItems.BLACK_STEEL_TOE_HIKING_BOOTS, new ResourceLocation(SacksNSuch.MODID, "item/hiking_boots"));
		basicItem(SNSItems.BLUE_STEEL_TOE_HIKING_BOOTS, new ResourceLocation(SacksNSuch.MODID, "item/hiking_boots"));
		basicItem(SNSItems.RED_STEEL_TOE_HIKING_BOOTS, new ResourceLocation(SacksNSuch.MODID, "item/hiking_boots"));
		basicItem(SNSItems.MOB_NET_ITEM);
		basicItem(SNSItems.STEEL_HORSESHOE);
		basicItem(SNSItems.STEEL_HORSESHOES);
		basicItem(SNSItems.BLACK_STEEL_HORSESHOE);
		basicItem(SNSItems.BLACK_STEEL_HORSESHOES);
		basicItem(SNSItems.BLUE_STEEL_HORSESHOE);
		basicItem(SNSItems.BLUE_STEEL_HORSESHOES);
		basicItem(SNSItems.RED_STEEL_HORSESHOE);
		basicItem(SNSItems.RED_STEEL_HORSESHOES);

		iconWithHeldModel(SNSItems.STRAW_BASKET);
		iconWithHeldModel(SNSItems.LEATHER_SACK,
				withExistingParent("item/held/leather_sack", SMALL_SACK).texture("sack", modLoc("item/held/leather_sack")));
		iconWithHeldModel(SNSItems.BURLAP_SACK,
				withExistingParent("item/held/burlap_sack", SMALL_SACK).texture("sack", modLoc("item/held/burlap_sack")));
		iconWithHeldModel(SNSItems.ORE_SACK, withExistingParent("item/held/ore_sack", LARGE_SACK).texture("sack", modLoc("item/held/ore_sack")));
		iconWithHeldModel(SNSItems.SEED_POUCH,
				withExistingParent("item/held/seed_pouch", SMALL_SACK).texture("sack", modLoc("item/held/seed_pouch")));
		iconWithHeldModel(SNSItems.FRAME_PACK);
		basicItem(SNSItems.LUNCHBOX);
		basicItem(SNSItems.QUIVER);
	}

	@Override
	public ItemModelBuilder getBuilder(final String path) {
		Preconditions.checkNotNull(path, "Path must not be null");
		final ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? mcLoc(path) : modLoc(path));
		existingFileHelper.trackGenerated(outputLoc, MODEL);
		return generatedModels.computeIfAbsent(outputLoc, factory);
	}

	@CanIgnoreReturnValue
	public ItemModelBuilder basicItem(final Supplier<? extends Item> item) {
		return basicItem(item.get());
	}

	@CanIgnoreReturnValue
	public ItemModelBuilder basicItem(final Supplier<? extends Item> item, final ResourceLocation texture) {
		return basicItem(item.get(), texture);
	}

	private ResourceLocation extendWithFolder(final ResourceLocation rl) {
		if (rl.getPath().startsWith(folder)) return rl;
		return rl.withPrefix(folder + "/");
	}

	@CanIgnoreReturnValue
	public ItemModelBuilder basicItem(final Item item, final ResourceLocation texture) {
		return basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), texture);
	}

	@CanIgnoreReturnValue
	public ItemModelBuilder basicItem(final ResourceLocation item, final ResourceLocation texture) {
		return getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
	}

	@CanIgnoreReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Supplier<? extends Item> item) {
		return iconWithHeldModel(item.get());
	}

	@CanIgnoreReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Item item) {
		return iconWithHeldModel(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	@CanIgnoreReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item) {
		return iconWithHeldModel(item, getExistingFile(item.withPrefix(ITEM_FOLDER + "/held/")));
	}

	@CanIgnoreReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Supplier<? extends Item> item, final ModelFile heldModel) {
		return iconWithHeldModel(item.get(), heldModel);
	}

	@CanIgnoreReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Item item, final ModelFile heldModel) {
		return iconWithHeldModel(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), heldModel);
	}

	@CanIgnoreReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item, final ModelFile heldModel) {
		return iconWithHeldModel(item, heldModel, icon(item));
	}

	@CanIgnoreReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item, final ModelFile heldModel,
			final ModelFile iconModel) {
		return getTransformedItemModelBuilder(item).base(nested().parent(heldModel))
				.perspective(ItemDisplayContext.GUI, nested().parent(iconModel))
				.perspective(ItemDisplayContext.GROUND, nested().parent(iconModel))
				.perspective(ItemDisplayContext.FIXED, nested().parent(iconModel));
	}

	@CanIgnoreReturnValue
	@SuppressWarnings("unused")
	private ItemModelBuilder icon(final Item item) {
		return icon(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	@CanIgnoreReturnValue
	private ItemModelBuilder icon(final ResourceLocation item) {
		return getBuilder(item.withPrefix("item/icon/").toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", item.withPrefix("item/icon/"));
	}

	@CheckReturnValue
	@SuppressWarnings("unused")
	private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(final Supplier<? extends Item> item) {
		return getTransformedItemModelBuilder(item.get());
	}

	@CheckReturnValue
	@SuppressWarnings("unused")
	private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(final Item item) {
		return getTransformedItemModelBuilder(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	@CheckReturnValue
	private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(final ResourceLocation key) {
		return getBuilder(key.toString()).parent(getExistingFile(new ResourceLocation("forge", "item/default")))
				.customLoader(SeparateTransformsModelBuilder::begin);
	}
}