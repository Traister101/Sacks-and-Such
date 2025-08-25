package mod.traister101.sns.network;

import mod.traister101.sns.common.capability.SNSCapabilities;
import mod.traister101.sns.common.menu.ContainerItemMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.Nullable;

public class ServerboundToggleSlotVoidingPacket {

	private final int slotIndex;

	public ServerboundToggleSlotVoidingPacket(final int slotIndex) {
		this.slotIndex = slotIndex;
	}

	ServerboundToggleSlotVoidingPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.slotIndex = friendlyByteBuf.readVarInt();
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeVarInt(slotIndex);
	}

	void handle(final @Nullable ServerPlayer player) {
		if (player == null) return;

		if (!(player.containerMenu instanceof final ContainerItemMenu containerItemMenu)) return;

		containerItemMenu.getContainerStack()
				.getCapability(SNSCapabilities.ITEM_VOIDER)
				.resolve()
				.ifPresent(itemVoider -> itemVoider.toggleVoidSlot(slotIndex));
	}
}