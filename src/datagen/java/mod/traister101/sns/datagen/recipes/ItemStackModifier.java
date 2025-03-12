package mod.traister101.sns.datagen.recipes;

import com.google.gson.*;

import net.minecraft.world.item.Item;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public interface ItemStackModifier {

	static ItemStackModifier simple(final String modifierId) {
		return () -> {
			final JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", modifierId);
			return jsonObject;
		};
	}

	static JsonObject writeItemStackProvider(final Item item, final int count, final List<ItemStackModifier> itemStackModifiers) {
		final var itemResult = new JsonObject();
		{
			final var stack = new JsonObject();
			stack.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString());
			if (count > 1) stack.addProperty("count", count);
			itemResult.add("stack", stack);
			{
				final var modifiers = new JsonArray();
				itemStackModifiers.forEach(stackModifier -> modifiers.add(stackModifier.toJson()));
				itemResult.add("modifiers", modifiers);
			}
		}
		return itemResult;
	}

	JsonElement toJson();
}