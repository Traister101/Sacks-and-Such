package mod.traister101.sacks.objects.recipes;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

import static mod.traister101.sacks.ConfigSNS.*;

@SuppressWarnings("unused")
public class ConditionalFactory implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String type = JsonUtils.getString(json, "config");

        switch (type) {
            case "thatch_sack":
                return () -> THATCH_SACK.isEnabled;
            case "leather_sack":
                return () -> LEATHER_SACK.isEnabled;
            case "burlap_sack":
                return () -> BURLAP_SACK.isEnabled;
            case "miner_sack":
                return () -> MINER_SACK.isEnabled;
            case "farmer_sack":
                return () -> FARMER_SACK.isEnabled;
            case "knap_sack":
                return () -> KNAP_SACK.isEnabled;
            case "reinforced_fiber":
                return () -> FARMER_SACK.isEnabled || MINER_SACK.isEnabled;
            case "steel_reinforced_fiber":
                return () -> (FARMER_SACK.isEnabled || MINER_SACK.isEnabled) && KNAP_SACK.isEnabled;
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