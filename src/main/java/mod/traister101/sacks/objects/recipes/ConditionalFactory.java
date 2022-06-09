package mod.traister101.sacks.objects.recipes;

import static mod.traister101.sacks.ConfigSNS.BURLAPSACK;
import static mod.traister101.sacks.ConfigSNS.LEATHERSACK;
import static mod.traister101.sacks.ConfigSNS.MINERSACK;
import static mod.traister101.sacks.ConfigSNS.THATCHSACK;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConditionalFactory implements IConditionFactory {
	
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "config");
		
		if (type == "thatch_sack") return () -> THATCHSACK.isEnabled;
		if (type == "leather_sack") return () -> LEATHERSACK.isEnabled;
		if (type == "burlap_sack") return () -> BURLAPSACK.isEnabled;
		if (type == "miner_sack") return () -> MINERSACK.isEnabled;
		// Fail safe if recipe doesn't have a config entry (everything should for this mod)
		return () -> false;
	}
}