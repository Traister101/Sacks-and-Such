package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.ConfigSNS;
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
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public final class PickupHandler {

    @SubscribeEvent
    public void onPickupItem(@Nonnull final EntityItemPickupEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (doPickupHanlding(player, event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockActivated(@Nonnull final RightClickBlock event) {
        final BlockPos blockPos = event.getPos();
        final World world = event.getWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() instanceof BlockPlacedItemFlat) {
            final TEPlacedItemFlat te = Helpers.getTE(world, blockPos, TEPlacedItemFlat.class);
            final ItemStack stack = te.getStack();
            te.setStack(ItemStack.EMPTY);
            world.setBlockToAir(blockPos);
            final EntityPlayer player = event.getEntityPlayer();
            if (event.getSide() == Side.SERVER) {
                final EntityItem itemEntity = new EntityItem(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
                if (!doPickupHanlding(player, itemEntity)) {
                    ItemHandlerHelper.giveItemToPlayer(player, stack);
                }
            }
            player.swingArm(EnumHand.MAIN_HAND);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static boolean doPickupHanlding(final EntityPlayer player, final EntityItem itemEntity) {
        final ItemStack itemPickup = itemEntity.getItem();

        if (topsOffPlayerInventory(player, itemPickup)) return true;

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack itemContainer = player.inventory.getStackInSlot(i);

            // Handle item pickups for all items with containers
            if (!ConfigSNS.GLOBAL.allPickup)
                // Not a sack
                if (!(itemContainer.getItem() instanceof ItemSack)) continue;

            // Make sure the item is a Sack, so we don't crash when other items can pickup too
            if (itemContainer.getItem() instanceof ItemSack) {
                SackType type = ((ItemSack) itemContainer.getItem()).getType();
                // Config pickup disabled for sack type
                if (!type.doesAutoPickup) continue;
                // This sack in particular has auto pickup disabled
                if (!SNSUtils.isAutoPickup(itemContainer)) continue;
            }

            IItemHandler containerInv = SNSUtils.getHandler(itemContainer);
            // Can't place in sack for any number of reasons
            if (!canPlaceInSack(containerInv, itemPickup)) continue;

            // Goes through the sack slots to see if the picked up item can be added
            for (int j = 0; j < containerInv.getSlots(); j++) {
                if (containerInv.getStackInSlot(j).getCount() < containerInv.getSlotLimit(j)) {
                    ItemStack pickupResult = containerInv.insertItem(j, itemPickup, false);
                    final int numPickedUp = itemPickup.getCount() - pickupResult.getCount();
                    itemEntity.setItem(pickupResult);

                    if (numPickedUp > 0) {
                        playPickupSound(player);
                        SPacketCollectItem packet = new SPacketCollectItem(itemEntity.getEntityId(), player.getEntityId(), numPickedUp);
                        ((EntityPlayerMP) player).connection.sendPacket(packet);
                        player.openContainer.detectAndSendChanges();
                        return true;
                    }
                }
            }

            // This defaults to false, sack config takes priority
            final boolean doVoiding = itemContainer.getItem() instanceof ItemSack && ((ItemSack) itemContainer.getItem()).getType().doesVoiding;
            if (tryItemVoid(doVoiding, itemPickup, itemContainer, player)) {
                return true;
            }
        }
        // If we get here we don't handle the item
        return false;
    }

    // TODO oops this empties any picked up stacks that could be valid not just the ones which match the current items
    // If this sack has voiding enabled empty the picked up stack and finish.
    // This means the first valid sack that has voiding enabled will void the pickup stack
    private static boolean tryItemVoid(boolean doVoiding, ItemStack itemPickup, ItemStack itemContainer, EntityPlayer player) {
        // Item voiding enabled
        if (ConfigSNS.GLOBAL.doVoiding)
            // Type can void items
            if (doVoiding)
                // This particular Sack has voiding enabled
                if (SNSUtils.isAutoVoid(itemContainer)) {
                    itemPickup.setCount(0);
                    playPickupSound(player);
                    return true;
                }
        return false;
    }

    private static boolean canPlaceInSack(@Nonnull IItemHandler containerInv, @Nonnull ItemStack itemPickup) {
        for (int j = 0; j < containerInv.getSlots(); j++) {
            if (containerInv.isItemValid(j, itemPickup)) return true;
        }
        return false;
    }

    // Tops off stuff in player inventory
    private static boolean topsOffPlayerInventory(@Nonnull final EntityPlayer player, @Nonnull final ItemStack stack) {
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