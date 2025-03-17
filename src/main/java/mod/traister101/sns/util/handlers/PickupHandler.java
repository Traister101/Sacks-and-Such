package mod.traister101.sns.util.handlers;

import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.*;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.rock.LooseRockBlock;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public final class PickupHandler {

	/**
	 * Intercept item pickups to try and place them into sacks
	 */
	public static void onPickupItem(final EntityItemPickupEvent event) {
		if (!SNSConfig.SERVER.doPickup.get()) return;

		final Player player = event.getEntity();
		final ItemEntity itemEntity = event.getItem();

		final ItemStack entityStack = itemEntity.getItem();
		final int startCount = entityStack.getCount();
		final ItemStack itemResult = pickupItemStack(player, entityStack);
		final int pickupCount = startCount - itemResult.getCount();

		// Picked up more than 0
		if (0 < pickupCount) {
			player.containerMenu.broadcastChanges();
			player.take(itemEntity, pickupCount);

			// Update the item entity
			if (itemResult.isEmpty()) {
				itemEntity.discard();
			} else {
				itemEntity.setItem(itemResult);
			}

			player.awardStat(Stats.ITEM_PICKED_UP.get(entityStack.getItem()), pickupCount);
			player.onItemPickup(itemEntity);
		}

		event.setCanceled(itemResult.isEmpty());
		event.setResult(0 < pickupCount ? Result.ALLOW : Result.DEFAULT);
	}

	/**
	 * Intercept block right clicks, so we can yoink TFC ground items
	 */
	public static void onGroundBlockInteract(final RightClickBlock event) {
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
	 * Our item pickup handling
	 *
	 * @param player Player to handle
	 * @param itemPickup The item being picked up
	 *
	 * @return The remainder
	 */
	private static ItemStack pickupItemStack(final Player player, final ItemStack itemPickup) {
		final var playerInventoryHandler = new PlayerMainInvWrapper(player.getInventory());
		var remainder = SNSUtils.insertItemOnlyStacked(playerInventoryHandler, itemPickup);

		if (remainder.isEmpty()) return ItemStack.EMPTY;

		if (SNSUtils.isCuriosPresent()) {
			final ItemStack finalRemainder = remainder;
			remainder = CuriosApi.getCuriosInventory(player)
					.map(ICuriosItemHandler::getEquippedCurios)
					.map(equippedCurios -> insertItemPickup(equippedCurios, finalRemainder))
					.orElse(remainder);

			if (remainder.isEmpty()) return ItemStack.EMPTY;
		}

		return insertItemPickup(playerInventoryHandler, remainder);
	}

	/**
	 * Inserts the picked up item into the provided {@link IItemHandler}
	 *
	 * @param itemHandler The {@link IItemHandler} to insert into
	 * @param itemPickup The {@link ItemStack} to pickup
	 *
	 * @return The remainder
	 */
	private static ItemStack insertItemPickup(final IItemHandler itemHandler, final ItemStack itemPickup) {
		ItemStack remainder = itemPickup;
		for (final var handlerSlot : SNSUtils.itemHandlerSlotIterator(itemHandler)) {
			if (!ContainerType.canDoItemPickup(handlerSlot.getStack())) continue;

			final var maybeContainerInv = handlerSlot.getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();

			if (maybeContainerInv.isEmpty()) continue;

			remainder = ItemHandlerHelper.insertItem(maybeContainerInv.get(), remainder, false);

			if (remainder.isEmpty()) return ItemStack.EMPTY;
		}
		return remainder;
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