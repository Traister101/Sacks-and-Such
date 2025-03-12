package mod.traister101.sns.mixins.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

@Mixin(AbstractHorse.class)
public interface AbstractHorseInventoryAccessor {

	@Accessor
	SimpleContainer getInventory();
}