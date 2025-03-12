package mod.traister101.sns.datagen.tfc.data;

import com.google.gson.JsonObject;

import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public abstract class ItemHeatProvider extends SimpleDataProvider {

	private final String modid;

	public ItemHeatProvider(final PackOutput output, final String modid) {
		super(output.createPathProvider(Target.DATA_PACK, "tfc/item_heats"));
		this.modid = modid;
	}

	protected final void ingotHeat(final Item item, final MetalData metalData, final int mb) {
		heat(item, metalData.ingotHeatCapacity(), metalData.getMeltTemp(), mb);
	}

	protected final void specificHeatCapacity(final Item item, final float specificCapacity, final float meltingTemp, final int mb) {
		final var forgingTemp = Math.round(meltingTemp * 0.6);
		final var weldingTemp = Math.round(meltingTemp * 0.8);
		final float heatCapacity = (float) Math.round(10 * specificCapacity * mb) / 1_000;
		heat(item, heatCapacity, forgingTemp, weldingTemp);
	}

	protected final void heat(final Item item, final float heatCapacity) {
		heat(item, heatCapacity, 0, 0);
	}

	protected final void heat(final Item item, final float heatCapacity, final float forgingTemp, final float weldingTemp) {
		heat(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), Ingredient.of(item), heatCapacity, forgingTemp, weldingTemp);
	}

	protected final void heat(final String name, final Ingredient ingredient, final float heatCapacity, final float forgingTemp,
			final float weldingTemp) {
		heat(new ResourceLocation(modid, name), ingredient, heatCapacity, forgingTemp, weldingTemp);
	}

	protected final void heat(final ResourceLocation location, final Ingredient ingredient, final float heatCapacity, final float forgingTemp,
			final float weldingTemp) {
		final var jsonObject = new JsonObject();
		jsonObject.add("ingredient", ingredient.toJson());
		jsonObject.addProperty("heat_capacity", heatCapacity);
		if (0 < forgingTemp) jsonObject.addProperty("forging_temperature", forgingTemp);
		if (0 < weldingTemp) jsonObject.addProperty("welding_temperature", weldingTemp);
		add(location, jsonObject);
	}

	@Override
	protected final void addData() {
		addHeats();
	}

	protected abstract void addHeats();

	@Override
	public String getName() {
		return "ItemHeat: " + modid;
	}
}