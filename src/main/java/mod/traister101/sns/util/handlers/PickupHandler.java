package mod.traister101.sns.util.handlers;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.rock.LooseRockBlock;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.*;

import java.util.Optional;

public final class PickupHandler {

	/**
	 * Intercept item pickups to try and place them into sacks
	 */
	public static void onPickupItem(final EntityItemPickupEvent event) {
		if (!SNSConfig.SERVER.doPickup.get()) return;

		final Player player = event.getEntity();
		final ItemEntity itemEntity = event.getItem();
		final ItemStack itemResult;
		final int pickupCount;
		{
			final ItemStack stack = itemEntity.getItem();
			final int startCount = stack.getCount();
			itemResult = pickupItemStack(player, stack);
			pickupCount = startCount - itemResult.getCount();
		}

		// Update the item entity
		itemEntity.setItem(itemResult);

		// Picked up more than 0
		if (0 < pickupCount) {
			player.containerMenu.broadcastChanges();
			final var packet = new ClientboundTakeItemEntityPacket(itemEntity.getId(), player.getId(), pickupCount);
			((ServerPlayer) player).connection.send(packet);
		}

		event.setCanceled(itemResult.isEmpty());
	}

	/**
	 * Intercept block right clicks, so we can yoink TFC ground items
	 */
	public static void onBlockActivated(final RightClickBlock event) {
		if (!SNSConfig.SERVER.doPickup.get()) return;

		final BlockPos blockPos = event.getPos();
		final Level level = event.getLevel();
		final BlockState blockState = level.getBlockState(blockPos);
		final Block block = blockState.getBlock();

		if (!(block instanceof GroundcoverBlock)) return;

		final Player player = event.getEntity();

		if (block instanceof LooseRockBlock) {
			if (player.getMainHandItem().getItem() == block.asItem()) {
				return;
			}
		}

		if (block instanceof FallenLeavesBlock) {
			if (player.getMainHandItem().getItem() == block.asItem()) {
				return;
			}
			if (blockState.getValue(FallenLeavesBlock.LAYERS) > 0) {
				return;
			}
		}

		if (level instanceof ServerLevel serverLevel) {
			Block.getDrops(blockState, serverLevel, blockPos, level.getBlockEntity(blockPos), player, ItemStack.EMPTY).forEach(itemStack -> {
				final ItemStack itemResult = pickupItemStack(player, itemStack);

				if (!itemResult.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, itemResult);
				} else {
					playPickupSound(serverLevel, player.position());
				}

				if (itemResult.getCount() != itemStack.getCount()) player.containerMenu.broadcastChanges();
			});
		}
		level.removeBlock(blockPos, false);

		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
	}

	/**
	 * Tries to first fill any valid stacks in the player inventory then tries to fill any {@link ContainerItem}s. If both fail to consume the entire stack
	 * the remainer is returned
	 *
	 * @param player Player to handle
	 * @param itemPickup The item being picked up
	 *
	 * @return Empty {@link ItemStack} or the remainer.
	 */
	private static ItemStack pickupItemStack(final Player player, final ItemStack itemPickup) {
		ItemStack remainder = itemPickup.copy();

		final Inventory playerInventory = player.getInventory();
		if (topOffPlayerInventory(playerInventory, remainder)) return ItemStack.EMPTY;

		if (ModList.get().isLoaded(CuriosApi.MODID)) {
			final Optional<ICuriosItemHandler> optionalCuriosItemHandler = CuriosApi.getCuriosInventory(player).resolve();

			if (optionalCuriosItemHandler.isPresent()) {
				final ICuriosItemHandler curiosItemHandler = optionalCuriosItemHandler.get();
				final IItemHandlerModifiable equippedCurios = curiosItemHandler.getEquippedCurios();

				for (int slotIndex = 0; slotIndex < equippedCurios.getSlots(); slotIndex++) {
					final ItemStack itemContainer = equippedCurios.getStackInSlot(slotIndex);

					if (!ContainerType.canDoItemPickup(itemContainer)) continue;

					final Optional<IItemHandler> containerInv = itemContainer.getCapability(ForgeCapabilities.ITEM_HANDLER)
							.resolve()
							.filter(h -> isValidForContainer(h, itemPickup));

					if (containerInv.isEmpty()) continue;

					remainder = insertStack(remainder, containerInv.get());

					if (remainder.isEmpty()) continue;
					if (SNSConfig.SERVER.doVoiding.get() && !ContainerType.canDoItemVoiding(itemContainer)) continue;

					if (!voidedItem(remainder, containerInv.get())) continue;

					return ItemStack.EMPTY;
				}
			}
		}

		for (int slotIndex = 0; slotIndex < playerInventory.getContainerSize(); slotIndex++) {
			final ItemStack itemContainer = playerInventory.getItem(slotIndex);

			if (!ContainerType.canDoItemPickup(itemContainer)) continue;

			final Optional<IItemHandler> containerInv = itemContainer.getCapability(ForgeCapabilities.ITEM_HANDLER)
					.resolve()
					.filter(h -> isValidForContainer(h, itemPickup));

			if (containerInv.isEmpty()) continue;

			remainder = insertStack(remainder, containerInv.get());

			if (remainder.isEmpty()) continue;
			if (SNSConfig.SERVER.doVoiding.get() && !ContainerType.canDoItemVoiding(itemContainer)) continue;

			if (!voidedItem(remainder, containerInv.get())) continue;

			return ItemStack.EMPTY;
		}
		return remainder;
	}

	/**
	 * Tries to fill the provided handler until it runs out of capacity or the fillStack runs out.
	 *
	 * @param fillStack The stack to put into the {@link IItemHandler}. May be mutated
	 *
	 * @return The remaining items that didn't fit
	 */
	private static ItemStack insertStack(final ItemStack fillStack, final IItemHandler itemHandler) {
		ItemStack pickupResult = fillStack.copy();
		for (int slotIndex = 0; slotIndex < itemHandler.getSlots(); slotIndex++) {
			if (itemHandler.getStackInSlot(slotIndex).getCount() >= itemHandler.getSlotLimit(slotIndex)) continue;

			pickupResult = itemHandler.insertItem(slotIndex, pickupResult, false);

			if (pickupResult.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}
		return pickupResult;
	}

	/**
	 * @param itemStack The Item Stack to try and void. Will be modified if successful
	 *
	 * @return If the item was voided.
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean voidedItem(final ItemStack itemStack, final IItemHandler itemHandler) {
		// Make sure there's a slot with the same type of item before voiding the pickup
		for (int slotIndex = 0; slotIndex < itemHandler.getSlots(); slotIndex++) {
			final ItemStack slotStack = itemHandler.getStackInSlot(slotIndex);
			if (!ItemStack.isSameItem(slotStack, itemStack)) continue;

			itemStack.setCount(0);
			return true;
		}
		return false;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isValidForContainer(final IItemHandler containerInv, final ItemStack itemPickup) {
		for (int slotIndex = 0; slotIndex < containerInv.getSlots(); slotIndex++) {
			if (containerInv.isItemValid(slotIndex, itemPickup)) return true;
		}
		return false;
	}

	/**
	 * Tops off the player inventory consuming the itemstack until all stacks in the inventory are filled
	 *
	 * @param inventoryPlayer Player inventory we should top up
	 * @param itemStack The itemstack we consume to fill the inventory
	 *
	 * @return If the item stack was fully consumed
	 */
	private static boolean topOffPlayerInventory(final Inventory inventoryPlayer, final ItemStack itemStack) {
		// Add to player inventory first, if there is an incomplete stack in there.
		for (int i = 0; i < inventoryPlayer.getContainerSize(); i++) {
			final ItemStack inventoryStack = inventoryPlayer.getItem(i);

			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) continue;

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) continue;

			// Can merge stacks
			if (ItemStack.isSameItemSameTags(inventoryStack, itemStack)) {
				final int remainingSpace = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (remainingSpace >= itemStack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(itemStack.getCount());
					itemStack.setCount(0);
					return true;
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					itemStack.shrink(remainingSpace);
				}
			}
		}
		return false;
	}

	/**
	 * Take a guess
	 *
	 * @param level The level to play the sound in
	 * @param pos The position to play the sound at
	 */
	private static void playPickupSound(final Level level, final Vec3 pos) {
		final var rand = level.random;
		level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
				((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
	}
}