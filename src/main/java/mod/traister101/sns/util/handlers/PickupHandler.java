package mod.traister101.sns.util.handlers;

import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.rock.LooseRockBlock;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
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
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.ModList;
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
		event.setResult(Result.ALLOW);
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
	 * Our item pickup handling
	 *
	 * @param player Player to handle
	 * @param itemPickup The item being picked up
	 *
	 * @return The remainer
	 */
	private static ItemStack pickupItemStack(final Player player, final ItemStack itemPickup) {
		ItemStack remainder = itemPickup;

		final Inventory inventory = player.getInventory();
		remainder = topOffPlayerInventory(inventory, remainder);

		if (remainder.isEmpty()) return ItemStack.EMPTY;

		if (ModList.get().isLoaded(CuriosApi.MODID)) {
			final var maybeCuriosItemHandler = CuriosApi.getCuriosInventory(player).resolve();

			if (maybeCuriosItemHandler.isPresent()) {
				final var curiosItemHandler = maybeCuriosItemHandler.get();
				final var equippedCurios = curiosItemHandler.getEquippedCurios();

				remainder = insertItemPickup(equippedCurios, remainder, equippedCurios.getSlots());
				if (remainder.isEmpty()) return ItemStack.EMPTY;
			}
		}

		remainder = insertItemPickup(new PlayerMainInvWrapper(inventory), remainder, Inventory.INVENTORY_SIZE);

		return remainder;
	}

	/**
	 * Inserts the picked up item into the provided {@link IItemHandler}
	 *
	 * @param itemHandler The {@link IItemHandler} to insert into
	 * @param itemPickup The {@link ItemStack} to pickup
	 * @param slotCount The slot count
	 *
	 * @return The remaining items
	 */
	private static ItemStack insertItemPickup(final IItemHandler itemHandler, final ItemStack itemPickup, final int slotCount) {
		ItemStack remainder = itemPickup;
		for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
			final ItemStack itemContainer = itemHandler.getStackInSlot(slotIndex);

			if (!ContainerType.canDoItemPickup(itemContainer)) continue;

			final var maybeContainerInv = itemContainer.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();

			if (maybeContainerInv.isEmpty()) continue;

			remainder = ItemHandlerHelper.insertItem(maybeContainerInv.get(), remainder, false);

			if (remainder.isEmpty()) return ItemStack.EMPTY;
			if (SNSConfig.SERVER.doVoiding.get() && !ContainerType.canDoItemVoiding(itemContainer)) continue;

			if (!voidedItem(remainder, maybeContainerInv.get())) return ItemStack.EMPTY;

			return ItemStack.EMPTY;
		}
		return remainder;
	}

	/**
	 * @param itemStack The Item Stack to try and void
	 *
	 * @return If the item was voided
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean voidedItem(final ItemStack itemStack, final IItemHandler itemHandler) {
		// Make sure there's a slot with the same type of item before voiding the pickup
		for (int slotIndex = 0; slotIndex < itemHandler.getSlots(); slotIndex++) {
			final ItemStack slotStack = itemHandler.getStackInSlot(slotIndex);
			if (!ItemStack.isSameItem(slotStack, itemStack)) continue;
			return true;
		}
		return false;
	}

	/**
	 * Tops off the player inventory consuming the itemstack until all stacks in the inventory are filled
	 *
	 * @param inventoryPlayer Player inventory handler
	 * @param insertStack The {@link ItemStack} we insert into the inventory
	 *
	 * @return If the item stack was fully consumed
	 */
	private static ItemStack topOffPlayerInventory(final Inventory inventoryPlayer, final ItemStack insertStack) {
		ItemStack remainder = insertStack;
		// Add to player inventory first, if there is an incomplete stack in there.
		for (int slotIndex = 0; slotIndex < inventoryPlayer.getContainerSize(); slotIndex++) {
			final ItemStack inventoryStack = inventoryPlayer.getItem(slotIndex);

			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) continue;

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) continue;

			// Can merge stacks
			if (ItemStack.isSameItemSameTags(inventoryStack, remainder)) {
				final int remainingSpace = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (remainingSpace >= remainder.getCount()) {
					// Enough space to add all
					inventoryStack.grow(remainder.getCount());
					return ItemStack.EMPTY;
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					remainder = ItemHandlerHelper.copyStackWithSize(remainder, remainder.getCount() - remainingSpace);
				}
			}
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