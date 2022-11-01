package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.util.SNSUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;

public class PickBlockHandler {

    public static void handlePickBlock(final EntityPlayer player) {

        if (!ConfigSNS.doPickBlock) return;

        final Minecraft mc = Minecraft.getMinecraft();
        final World world = mc.world;
        final RayTraceResult target = mc.objectMouseOver;

        if (ForgeHooks.onPickBlock(target, player, world)) return;

        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            final IBlockState state = world.getBlockState(target.getBlockPos());
            // Is air block
            if (state.getBlock().isAir(state, world, target.getBlockPos())) return;

            final ItemStack stackToSelect = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);

            if (stackToSelect.isEmpty()) return;

            if (!isEmptySlot(player, stackToSelect)) return;

            final ItemStack foundStack = findStackInItemContainer(player, stackToSelect);

            if (foundStack.isEmpty()) return;

            player.inventory.addItemStackToInventory(foundStack);

            final int slot = player.inventory.getSlotFor(stackToSelect);
            if (slot != -1) {
                if (InventoryPlayer.isHotbar(slot)) {
                    player.inventory.currentItem = slot;
                } else Minecraft.getMinecraft().playerController.pickItem(slot);
            }
        }
    }


    // Finds matching stack inside a container or returns an empty stack
    private static ItemStack findStackInItemContainer(final EntityPlayer player, final ItemStack stackToMatch) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            final ItemStack inventoryStack = player.inventory.getStackInSlot(i);

            // Only sacks do pick block
            if (!ConfigSNS.GLOBAL.allPickBlock) {
                // Not a sack
                if (!(inventoryStack.getItem() instanceof ItemSack)) continue;
            }

            final IItemHandler containerInv = SNSUtils.getHandler(inventoryStack);

            for (int j = 0; j < containerInv.getSlots(); j++) {
                final ItemStack stack = containerInv.getStackInSlot(j);
                if (!stack.isItemEqual(stackToMatch)) continue;
                final int extractAmount = stack.getMaxStackSize();
                return containerInv.extractItem(j, extractAmount, false);
            }
        }
        return ItemStack.EMPTY;
    }

    // True if empty slot in player inventory or there's a slot that matches the pickblock
    private static boolean isEmptySlot(final EntityPlayer player, final ItemStack stackToMatch) {
        final InventoryPlayer playerInv = player.inventory;
        for (int i = 0; i < playerInv.getSizeInventory(); i++) {
            final ItemStack compareStack = playerInv.getStackInSlot(i);
            if (compareStack.isEmpty() || stackToMatch.isItemEqual(compareStack)) return true;
        }
        return false;
    }
}
