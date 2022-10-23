package mod.traister101.sacks.util.handlers;

import mcp.MethodsReturnNonnullByDefault;
import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.TogglePacket;
import mod.traister101.sacks.objects.inventory.capability.SackHandler;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PickupHandler {

    @SubscribeEvent
    public void onPickupItem(final EntityItemPickupEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (doPickupHanlding(player, event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockActivated(final RightClickBlock event) {
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
            final ItemStack itemContainer = player.inventory.getStackInSlot(i);

            // Handle item pickups for all items with containers
            if (!ConfigSNS.GLOBAL.allPickup)
                // Not a sack
                if (!(itemContainer.getItem() instanceof ItemSack)) continue;

            // Make sure the item is a Sack, so we don't crash when other items can pick up too
            if (itemContainer.getItem() instanceof ItemSack) {
                SackType type = ((ItemSack) itemContainer.getItem()).getType();
                // Config pickup disabled for sack type
                if (!type.doesAutoPickup) continue;
                // This sack in particular has auto pickup disabled
                if (!SNSUtils.isAutoPickup(itemContainer)) continue;
            }

            final IItemHandler containerInv = SNSUtils.getHandler(itemContainer);
            // Can't place in sack for any number of reasons
            if (!canPlaceInSack(containerInv, itemPickup)) continue;

            // Goes through the sack slots to see if the picked up item can be added
            for (int j = 0; j < containerInv.getSlots(); j++) {
                if (containerInv.getStackInSlot(j).getCount() < containerInv.getSlotLimit(j)) {
                    final ItemStack pickupResult = containerInv.insertItem(j, itemPickup, false);
                    final int numPickedUp = itemPickup.getCount() - pickupResult.getCount();
                    itemEntity.setItem(pickupResult);

                    if (numPickedUp > 0) {
                        playPickupSound(player);
                        SPacketCollectItem packet = new SPacketCollectItem(itemEntity.getEntityId(), player.getEntityId(), numPickedUp);
                        ((EntityPlayerMP) player).connection.sendPacket(packet);
                        player.openContainer.detectAndSendChanges();
                        if (containerInv instanceof SackHandler) {
                            final boolean toggleFlag = ((SackHandler) containerInv).hasItems();
                            SacksNSuch.getNetwork().sendToServer(new TogglePacket(toggleFlag, SNSUtils.ToggleType.ITEMS));
                        }
                        return true;
                    }
                }
            }
            // No slots were valid for incerting
            if (!canPlaceInSack(containerInv, itemPickup)) return false;
            // Can't void
            if (!canItemVoid(itemContainer)) return false;
            // Make sure there's a slot with the same type of item before voiding the pickup
            for (int j = 0; j < containerInv.getSlots(); j++) {
                final ItemStack slotStack = containerInv.getStackInSlot(j);
                if (ItemStack.areItemsEqual(slotStack, itemPickup)) {
                    itemPickup.setCount(0);
                    playPickupSound(player);
                    return true;
                }
            }
        }
        // If we get here we don't handle the item
        return false;
    }

    private static boolean canItemVoid(final ItemStack itemContainer) {
        // Item voiding disabled
        if (!ConfigSNS.GLOBAL.doVoiding) return false;
        // Not a sack
        if (!(itemContainer.getItem() instanceof ItemSack)) return false;
        // Type can't void items
        if (!((ItemSack) itemContainer.getItem()).getType().doesVoiding) return false;
        // Returns if this particular sack item has voiding enabled
        return SNSUtils.isAutoVoid(itemContainer);
    }

    private static boolean canPlaceInSack(final IItemHandler containerInv, final ItemStack itemPickup) {
        for (int j = 0; j < containerInv.getSlots(); j++) {
            if (containerInv.isItemValid(j, itemPickup)) return true;
        }
        return false;
    }

    // Tops off stuff in player inventory
    private static boolean topsOffPlayerInventory(final EntityPlayer player, final ItemStack stack) {
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