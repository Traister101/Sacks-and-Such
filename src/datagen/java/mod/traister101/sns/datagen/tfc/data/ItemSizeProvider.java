package mod.traister101.sns.datagen.tfc.data;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.capabilities.size.*;

import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class ItemSizeProvider extends SimpleDataProvider {

	private final String modid;

	public ItemSizeProvider(final PackOutput output, final String modid) {
		super(output.createPathProvider(Target.DATA_PACK, "tfc/item_sizes"));
		this.modid = modid;
	}

	protected final void itemSize(final Supplier<? extends Item> item, final Weight weight, final Size size) {
		size(item.get(), weight, size);
	}

	protected final void size(final Item item, final Weight weight, final Size size) {
		size(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), Ingredient.of(item), weight, size);
	}

	protected final void size(final String name, final Ingredient ingredient, final Weight weight,
			@SuppressWarnings("SameParameterValue") final Size size) {
		size(new ResourceLocation(modid, name), ingredient, weight, size);
	}

	protected final void size(final ResourceLocation location, final Ingredient ingredient, final Weight weight, final Size size) {
		final var jsonObject = new JsonObject();
		jsonObject.add("ingredient", ingredient.toJson());
		jsonObject.addProperty("size", size.name);
		jsonObject.addProperty("weight", weight.name);
		add(location, jsonObject);
	}

	protected abstract void addSizes();

	@Override
	protected final void addData() {
		addSizes();
	}

	@Override
	public String getName() {
		return "ItemSizes: " + modid;
	}
}