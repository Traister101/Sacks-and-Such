package mod.traister101.sns.network;

import mod.traister101.sns.common.capability.FoodHolder.CycleDirection;
import mod.traister101.sns.common.capability.SNSCapabilities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.Nullable;

public class ServerboundPacketCycleSlotPacket {

	private final CycleDirection cycleDirection;

	public ServerboundPacketCycleSlotPacket(final CycleDirection cycleDirection) {
		this.cycleDirection = cycleDirection;
	}

	ServerboundPacketCycleSlotPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.cycleDirection = friendlyByteBuf.readEnum(CycleDirection.class);
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeEnum(cycleDirection);
	}

	void handle(final @Nullable ServerPlayer player) {
		if (player == null) return;

		player.getMainHandItem().getCapability(SNSCapabilities.FOOD_HOLDER).ifPresent(foodHolder -> foodHolder.cycleSelected(cycleDirection));
	}
}