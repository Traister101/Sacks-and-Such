package mod.traister101.sns.network;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.common.items.HikingBootsItem;
import mod.traister101.sns.util.NBTHelper;
import mod.traister101.sns.util.SNSUtils.ToggleType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public final class ServerboundTogglePacket {

	private final boolean toggle;
	private final ToggleType type;

	public ServerboundTogglePacket(final boolean toggle, final ToggleType type) {
		this.toggle = toggle;
		this.type = type;
	}

	ServerboundTogglePacket(final FriendlyByteBuf friendlyByteBuf) {
		toggle = friendlyByteBuf.readBoolean();
		type = ToggleType.byId(friendlyByteBuf.readInt());
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeBoolean(toggle);
		friendlyByteBuf.writeInt(type.ordinal());
	}

	void handle(final @Nullable ServerPlayer player) {
		if (player == null) return;

		final ItemStack mainHandItem = player.getMainHandItem();
		if ((mainHandItem.getItem() instanceof final ContainerItem containerItem) && type.supportsContainerType(containerItem.type)) {
			NBTHelper.toggle(mainHandItem, type, toggle);
		}

		final ItemStack wornBoots = player.getItemBySlot(EquipmentSlot.FEET);
		if ((wornBoots.getItem() instanceof HikingBootsItem)) {
			NBTHelper.toggle(wornBoots, type, toggle);
		}
	}
}
