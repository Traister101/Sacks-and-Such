package mod.traister101.sacks.objects.recipes;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import mod.traister101.sacks.ConfigSNS;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;


public class ConditionalFactory implements IConditionFactory {
	
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "config");
		
		if (ConfigSNS.General.globalToggle) return () -> false;
		if (type == "thatch_sack") return () -> ConfigSNS.General.THATCHSACK.enabled;
		if (type == "leather_sack") return () -> ConfigSNS.General.LEATHERSACK.enabled;
		if (type == "burlap_sack") return () -> ConfigSNS.General.BURLAPSACK.enabled;
		if (type == "miner_sack") return () -> ConfigSNS.General.MINERSACK.enabled;
		// Fail safe if recipe doesn't have a config entry (everything should for this mod)
		return () -> false;
	}
}