package mod.traister101.sns.compat.curios;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public final class CuriosUtils {

	public static Optional<IItemHandler> getEquippedCurios(final LivingEntity entity) {
		return CuriosApi.getCuriosInventory(entity).map(ICuriosItemHandler::getEquippedCurios);
	}
}