package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SackType;
import net.dries007.tfc.objects.blocks.BlockPlacedItemFlat;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Random;

public final class PickupHandler {

	@SubscribeEvent
	public void onPickupItem(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();

		doPickupHanlding(player, event.getItem());
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onBlockActivated(RightClickBlock event) {
		BlockPos blockPos = event.getPos();
		World world = event.getWorld();
		IBlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof BlockPlacedItemFlat) {
			TEPlacedItemFlat te = Helpers.getTE(world, blockPos, TEPlacedItemFlat.class);
			ItemStack itemStack = te.getStack();
			te.setStack(ItemStack.EMPTY);
			world.setBlockToAir(blockPos);
			EntityPlayer player = event.getEntityPlayer();
			if (event.getSide() == Side.SERVER) {
				doPickupHanlding(player, new EntityItem(world, player.posX, player.posY, player.posZ, itemStack));
			}
			player.swingArm(EnumHand.MAIN_HAND);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	private static void doPickupHanlding(EntityPlayer player, EntityItem itemEntity) {
		final ItemStack itemPickup = itemEntity.getItem();

		if (topsOffPlayerInventory(player, itemPickup)) return;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack sackStack = player.inventory.getStackInSlot(i);
			// Not a sack
			if (!(sackStack.getItem() instanceof ItemSack)) continue;

			ItemSack sack = (ItemSack) sackStack.getItem();
			SackType type = sack.getType();

			// Config pickup disabled for sack type
			if (!type.doesAutoPickup) continue;
			// This sack in particular has auto pickup disabled
			if (!SNSUtils.isAutoPickup(sackStack)) continue;

			IItemHandler sackInv = SNSUtils.getHandler(sackStack);
			// Can't place in sack for any number of reasons
			if (!canPlaceInSack(type, sackInv, itemPickup)) continue;

			for (int j = 0; j < sackInv.getSlots(); j++) {
				if (sackInv.getStackInSlot(j).getCount() < sackInv.getSlotLimit(j)) {
					ItemStack pickupResult = sackInv.insertItem(j, itemPickup, false);
					final int numPickedUp = itemPickup.getCount() - pickupResult.getCount();
					itemEntity.setItem(pickupResult);

					if (numPickedUp > 0) {
						playPickupSound(player);
						SPacketCollectItem packet = new SPacketCollectItem(itemEntity.getEntityId(), player.getEntityId(), numPickedUp);
						((EntityPlayerMP) player).connection.sendPacket(packet);
						player.openContainer.detectAndSendChanges();
						return;
					}
				}
			}
			// If this sack has voiding enabled empty the picked up stack and finish.
			// This means the first valid sack that has voiding enabled will void the pickup stack
			if (SNSUtils.isAutoVoid(sackStack)) {
				itemPickup.setCount(0);
				playPickupSound(player);
				return;
			}
		}
	}

	private static boolean canPlaceInSack(@Nonnull SackType type, @Nonnull IItemHandler sackInv, @Nonnull ItemStack itemPickup) {
		for (int j = 0; j < type.slots; j++) {
			if (sackInv.isItemValid(j, itemPickup)) return true;
		}
		return false;
	}

	// Tops off stuff in player inventory
	private static boolean topsOffPlayerInventory(EntityPlayer player, @Nonnull ItemStack stack) {
		// Add to player inventory first, if there is an incomplete stack in there.
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = player.inventory.getStackInSlot(i);
			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) continue;
			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) continue;

			if (inventoryStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inventoryStack, stack)) {
				int space = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (space >= stack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(stack.getCount());
					stack.setCount(0);
					playPickupSound(player);
					return true;
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					stack.shrink(space);
				}
			}
		}
		return false;
	}

	// Take a guess
	private static void playPickupSound(final EntityPlayer player) {
		Random rand = player.world.rand;
		player.world.playSound(null, player.posX, player.posY, player.posZ,
				SoundEvents.ENTITY_ITEM_PICKUP,
				SoundCategory.PLAYERS, 0.2F,
				((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
	}
}