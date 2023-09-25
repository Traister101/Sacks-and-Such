package mod.traister101.sacks.objects.recipes;

import com.google.gson.JsonObject;
import mod.traister101.sacks.ConfigSNS;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

import static mod.traister101.sacks.ConfigSNS.SACK;

@SuppressWarnings("unused")
public class ConditionalFactory implements IConditionFactory {


	private final boolean THATCH_ENABLED = SACK.THATCH_SACK.DANGEROUS.isEnabled;
	private final boolean LEATHER_ENABLED = SACK.LEATHER_SACK.DANGEROUS.isEnabled;
	private final boolean BURLAP_ENABLED = SACK.BURLAP_SACK.DANGEROUS.isEnabled;
	private final boolean MINER_ENABLED = SACK.MINER_SACK.DANGEROUS.isEnabled;
	private final boolean FARMER_ENABLED = SACK.FARMER_SACK.DANGEROUS.isEnabled;
	private final boolean KNAP_ENABLED = SACK.FARMER_SACK.DANGEROUS.isEnabled;
	private final boolean VESSEL_ENABLED = ConfigSNS.EXPLOSIVE_VESSEL.DANGEROUS.isEnabled;
	private final boolean SMALL_ENABLED = ConfigSNS.EXPLOSIVE_VESSEL.DANGEROUS.smallEnabled;
	private final boolean STICKY_ENABLED = ConfigSNS.EXPLOSIVE_VESSEL.DANGEROUS.stickyEnabled;

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "config");

		switch (type) {
			case "thatch_sack":
				return () -> THATCH_ENABLED;
			case "leather_sack":
				return () -> LEATHER_ENABLED;
			case "burlap_sack":
				return () -> BURLAP_ENABLED;
			case "miner_sack":
				return () -> MINER_ENABLED;
			case "farmer_sack":
				return () -> FARMER_ENABLED;
			case "knap_sack":
				return () -> KNAP_ENABLED;
			case "reinforced_fiber":
				return () -> FARMER_ENABLED || MINER_ENABLED;
			case "steel_reinforced_fiber":
				return () -> (FARMER_ENABLED || MINER_ENABLED) && KNAP_ENABLED;
			case "tiny_vessel":
				return () -> VESSEL_ENABLED && SMALL_ENABLED;
			case "sticky_vessel":
				return () -> VESSEL_ENABLED && STICKY_ENABLED;
			// If a recipe uses the mod conditional but doesn't have a valid config key it won't work
			default:
				return () -> false;
		}
	}
}