package mod.traister101.sns.util;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public sealed interface ItemSlotData {

	static ItemSlotData read(final FriendlyByteBuf byteBuf) {
		return switch (byteBuf.readEnum(Type.class)) {
			case HELD -> new HeldSlotData(byteBuf.readBoolean());
			case INVENTORY -> new InventorySlotData(byteBuf.readVarInt());
			case CURIOS -> new CuriosSlotData(byteBuf.readUtf(), byteBuf.readVarInt());
		};
	}

	ItemStack stack(Player player);

	void write(FriendlyByteBuf friendlyByteBuf);

	Type type();

	enum Type {
		HELD,
		INVENTORY,
		CURIOS
	}

	record HeldSlotData(boolean mainHand) implements ItemSlotData {

		public HeldSlotData(final InteractionHand hand) {
			this(hand == InteractionHand.MAIN_HAND);
		}

		@Override
		public ItemStack stack(final Player player) {
			return mainHand ? player.getMainHandItem() : player.getOffhandItem();
		}

		@Override
		public void write(final FriendlyByteBuf friendlyByteBuf) {
			friendlyByteBuf.writeEnum(Type.HELD);
			friendlyByteBuf.writeBoolean(mainHand);
		}

		@Override
		public Type type() {
			return Type.HELD;
		}
	}

	record InventorySlotData(int slotIndex) implements ItemSlotData {

		@Override
		public ItemStack stack(final Player player) {
			return player.getInventory().getItem(slotIndex);
		}

		@Override
		public void write(final FriendlyByteBuf friendlyByteBuf) {
			friendlyByteBuf.writeEnum(Type.INVENTORY);
			friendlyByteBuf.writeVarInt(slotIndex);
		}

		@Override
		public Type type() {
			return Type.INVENTORY;
		}
	}

	record CuriosSlotData(String identifier, int slotIndex) implements ItemSlotData {

		@Override
		public ItemStack stack(final Player player) {
			return CuriosApi.getCuriosInventory(player)
					.resolve()
					.flatMap(iCuriosItemHandler -> iCuriosItemHandler.getStacksHandler(identifier))
					.map(ICurioStacksHandler::getStacks)
					.map(iDynamicStackHandler -> iDynamicStackHandler.getStackInSlot(slotIndex))
					.orElse(ItemStack.EMPTY);
		}

		@Override
		public void write(final FriendlyByteBuf friendlyByteBuf) {
			friendlyByteBuf.writeEnum(Type.CURIOS);
			friendlyByteBuf.writeUtf(identifier);
			friendlyByteBuf.writeVarInt(slotIndex);
		}

		@Override
		public Type type() {
			return Type.CURIOS;
		}
	}
}