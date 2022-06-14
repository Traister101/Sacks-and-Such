package mod.traister101.sacks.objects.recipes;

import static mod.traister101.sacks.ConfigSNS.*;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConditionalFactory implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "config");

		switch (type) {
		case "thatch_sack":
			return () -> THATCHSACK.isEnabled;
		case "leather_sack":
			return () -> LEATHERSACK.isEnabled;
		case "burlap_sack":
			return () -> BURLAPSACK.isEnabled;
		case "miner_sack":
			return () -> MINERSACK.isEnabled;
		case "tiny_vessel":
			return () -> EXPLOSIVE_VESSEL.isEnabled && EXPLOSIVE_VESSEL.smallEnabled;
		case "sticky_vessel":
			return () -> EXPLOSIVE_VESSEL.isEnabled && EXPLOSIVE_VESSEL.stickyEnabled;
		// If a recipe uses the mod conditional but doesn't have a valid config key it won't work
		default:
			return () -> false;
		}
	}
}