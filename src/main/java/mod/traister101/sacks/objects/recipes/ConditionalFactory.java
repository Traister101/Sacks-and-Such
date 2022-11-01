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
                return () -> THATCH_SACK.DANGEROUS.isEnabled;
            case "leather_sack":
                return () -> LEATHER_SACK.DANGEROUS.isEnabled;
            case "burlap_sack":
                return () -> BURLAP_SACK.DANGEROUS.isEnabled;
            case "miner_sack":
                return () -> MINER_SACK.DANGEROUS.isEnabled;
            case "farmer_sack":
                return () -> FARMER_SACK.DANGEROUS.isEnabled;
            case "knap_sack":
                return () -> KNAP_SACK.DANGEROUS.isEnabled;
            case "reinforced_fiber":
                return () -> FARMER_SACK.DANGEROUS.isEnabled || MINER_SACK.DANGEROUS.isEnabled;
            case "steel_reinforced_fiber":
                return () -> (FARMER_SACK.DANGEROUS.isEnabled || MINER_SACK.DANGEROUS.isEnabled) && KNAP_SACK.DANGEROUS.isEnabled;
            case "tiny_vessel":
                return () -> EXPLOSIVE_VESSEL.DANGEROUS.isEnabled && EXPLOSIVE_VESSEL.DANGEROUS.smallEnabled;
            case "sticky_vessel":
                return () -> EXPLOSIVE_VESSEL.DANGEROUS.isEnabled && EXPLOSIVE_VESSEL.DANGEROUS.stickyEnabled;
            // If a recipe uses the mod conditional but doesn't have a valid config key it won't work
            default:
                return () -> false;
        }
    }
}