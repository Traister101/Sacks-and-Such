package mod.traister101.sns.network;

import mod.traister101.sns.common.menu.SNSMenus;
import mod.traister101.sns.util.ItemSlotData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class ServerboundOpenContainerPacket {

	private final ItemSlotData slotData;

	ServerboundOpenContainerPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.slotData = ItemSlotData.read(friendlyByteBuf);
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		slotData.write(friendlyByteBuf);
	}

	void handle(final @Nullable ServerPlayer player) {
		if (player == null) return;

		SNSMenus.CONTAINER_ITEM_MENU_PROVIDER.openMenu(player, slotData);
	}
}