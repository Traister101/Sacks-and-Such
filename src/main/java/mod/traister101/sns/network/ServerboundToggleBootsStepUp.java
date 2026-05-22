package mod.traister101.sns.network;

import mod.traister101.sns.common.items.HikingBootsItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public final class ServerboundToggleBootsStepUp {

	public ServerboundToggleBootsStepUp() {}

	ServerboundToggleBootsStepUp(final FriendlyByteBuf friendlyByteBuf) {}

	void encode(final FriendlyByteBuf friendlyByteBuf) {}

	void handle(final @Nullable ServerPlayer player) {
		if (player == null) return;

		final var bootsStack = player.getItemBySlot(EquipmentSlot.FEET);
		if (!(bootsStack.getItem() instanceof HikingBootsItem)) return;
		// Just take the boots off to ensure things update correctly
		player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
		final var stepUpEnabled = HikingBootsItem.isStepUpEnabled(bootsStack);
		bootsStack.getOrCreateTag().putBoolean(HikingBootsItem.DISABLE_STEP_UP_NBT_KEY, stepUpEnabled);
		player.setItemSlot(EquipmentSlot.FEET, bootsStack);
	}
}