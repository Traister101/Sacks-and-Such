package mod.traister101.sns.network;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.common.menu.ContainerItemMenu;
import mod.traister101.sns.util.SNSUtils;
import top.theillusivec4.curios.api.*;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;

public class ServerboundOpenContainerPacket {

	public ServerboundOpenContainerPacket() {
	}

	ServerboundOpenContainerPacket(@SuppressWarnings("unused") final FriendlyByteBuf friendlyByteBuf) {

	}

	void encode(@SuppressWarnings("unused") final FriendlyByteBuf friendlyByteBuf) {

	}

	void handle(final @Nullable ServerPlayer player) {
		if (player == null) return;

		if (SNSUtils.isCuriosPresent()) {
			final var maybeSlotResult = CuriosApi.getCuriosInventory(player)
					.resolve()
					.flatMap(curiosItemHandler -> curiosItemHandler.findFirstCurio(itemStack -> itemStack.getItem() instanceof ContainerItem));
			if (maybeSlotResult.isPresent()) {
				final var slotResult = maybeSlotResult.get();
				final ItemStack itemStack = slotResult.stack();

				final var maybeItemHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();

				if (maybeItemHandler.isPresent()) {
					final SlotContext slotContext = slotResult.slotContext();

					NetworkHooks.openScreen(player, new SimpleMenuProvider(
									(pContainerId, pPlayerInventory, pPlayer) -> ContainerItemMenu.forUnIndexableStack(pContainerId, pPlayerInventory,
											maybeItemHandler.get(), slotResult::stack), itemStack.getHoverName()),
							ContainerItemMenu.writeCurios(slotContext.identifier(), slotContext.index()));
					return;
				}
			}
		}

		final Inventory inventory = player.getInventory();
		for (int slotIndex = inventory.getContainerSize() - 1; slotIndex >= 0; slotIndex--) {
			final ItemStack itemStack = inventory.getItem(slotIndex);

			if (!(itemStack.getItem() instanceof ContainerItem)) continue;

			final int finalSlotIndex = slotIndex;

			if (Inventory.isHotbarSlot(slotIndex)) {
				inventory.selected = slotIndex;
				player.connection.send(new ClientboundSetCarriedItemPacket(inventory.selected));
				final var maybeItemHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
				if (maybeItemHandler.isPresent()) {
					NetworkHooks.openScreen(player, new SimpleMenuProvider(
									(pContainerId, pPlayerInventory, pPlayer) -> ContainerItemMenu.forHeld(pContainerId, pPlayerInventory,
											maybeItemHandler.get(), InteractionHand.MAIN_HAND), itemStack.getHoverName()),
							ContainerItemMenu.writeHeld(InteractionHand.MAIN_HAND));
					return;
				}
			} else {
				final var maybeItemHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
				if (maybeItemHandler.isPresent()) {
					NetworkHooks.openScreen(player, new SimpleMenuProvider(
									(pContainerId, pPlayerInventory, pPlayer) -> ContainerItemMenu.forInventory(pContainerId, pPlayerInventory,
											maybeItemHandler.get(), finalSlotIndex), itemStack.getHoverName()),
							ContainerItemMenu.writeInventory(finalSlotIndex));
					return;
				}
			}
		}
	}
}