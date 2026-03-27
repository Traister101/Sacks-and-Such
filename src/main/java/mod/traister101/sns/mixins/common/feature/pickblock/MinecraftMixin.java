package mod.traister101.sns.mixins.common.feature.pickblock;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.network.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	/**
	 * @reason Forge doesn't have a good enough event to extend pick block functionality to extract from our Container Items
	 * @author Traister101
	 */
	@Definition(id = "i", local = @Local(type = int.class))
	@Expression("i != -1")
	@Inject(method = "pickBlock", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
	private void temp(final CallbackInfo ci, @Local final int i, @Local final ItemStack stack) {
		if (!SNSConfig.COMMON.doPickBlock.get()) return;
		if (i != Inventory.NOT_FOUND_INDEX) return;

		SNSPacketHandler.sendToServer(new ServerboundPickBlockPacket(stack));
	}
}