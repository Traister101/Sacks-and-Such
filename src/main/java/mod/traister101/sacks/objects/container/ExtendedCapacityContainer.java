package mod.traister101.sacks.objects.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ExtendedCapacityContainer extends Container {

	protected final IItemHandler handler;
	/**
	 * Shorthand for {@link IItemHandler#getSlots()} as we need the count in lots of places.
	 * This does issues if the handler dynamically resizes although that isn't really supported anyway
	 */
	protected final int slotCount;
	/** Index of the item container, so we can prevent moving it */
	protected final int heldItemIndex;

	public ExtendedCapacityContainer(final InventoryPlayer inventoryPlayer, final ItemStack heldStack) {
		{ // TODO refactor to just pass the handler in?
			if (!heldStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
				throw new IllegalArgumentException("The given ItemStack requires Item handling capability");

			this.handler = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			assert handler != null;
		}

		this.slotCount = handler.getSlots();

		// Initalize the heldItemIndex
		{
			final int heldItemIndex;
			if (heldStack == inventoryPlayer.player.getHeldItemMainhand()) {
				// Mainhand opened inventory
				heldItemIndex = inventoryPlayer.currentItem + 27;
			} else {
				// Offhand, so ignore this rule as we can't click it
				heldItemIndex = -100;
			}
			this.heldItemIndex = heldItemIndex + handler.getSlots();
		}

		addContainerSlots();
		addPlayerInventorySlots(inventoryPlayer);
	}

	@Override
	public final ItemStack transferStackInSlot(final EntityPlayer player, final int slotIdex) {
		final Slot slot = inventorySlots.get(slotIdex);

		if (slot.getHasStack()) {
			final ItemStack slotStack = slot.getStack();

			if (slotIdex < slotCount) {
				if (!mergeItemStack(slotStack, slotCount, inventorySlots.size(), true)) return ItemStack.EMPTY;
			} else if (!mergeItemStack(slotStack, 0, slotCount, false)) return ItemStack.EMPTY;

			if (slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else slot.onSlotChanged();
			return slotStack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public final ItemStack slotClick(final int slotIndex, final int dragType, final ClickType clickType, final EntityPlayer player) {
		// Not a slot, let vanilla handle it
		if (0 > slotIndex) return super.slotClick(slotIndex, dragType, clickType, player);

		// Prevent moving of the item stack that is currently open
		if (heldItemIndex == slotIndex) return ItemStack.EMPTY;

		// Keybind swap
		if (clickType == ClickType.SWAP && 0 <= dragType && 9 > dragType) {
			final ItemStack hoverStack = inventorySlots.get(slotIndex).getStack();
			final ItemStack hotbarStack = player.inventory.getStackInSlot(dragType);
			// Blocks the held stack from being moved
			if (hoverStack == player.getHeldItemMainhand() || player.getHeldItemMainhand() == hotbarStack) return ItemStack.EMPTY;

			// If either stack is empty pass it to vanilla
			if (hotbarStack.isEmpty() || hoverStack.isEmpty()) return super.slotClick(slotIndex, dragType, clickType, player);

			// If neither stack is too large pass to vanilla
			if (hotbarStack.getCount() <= hotbarStack.getMaxStackSize() && hoverStack.getCount() <= hoverStack.getMaxStackSize())
				return super.slotClick(slotIndex, dragType, clickType, player);

			return ItemStack.EMPTY;
		}

		// Vanilla slot, pass to vanilla
		if (slotCount - 1 < slotIndex) return super.slotClick(slotIndex, dragType, clickType, player);

		final Slot slot = getSlot(slotIndex);

		// Shift click
		if (clickType == ClickType.QUICK_MOVE) {
			if (!slot.canTakeStack(player)) return ItemStack.EMPTY;

			return transferStackInSlot(player, slotIndex);
		}

		final ItemStack slotStack = slot.getStack();

		// Pressing q
		if (clickType == ClickType.THROW && player.inventory.getItemStack().isEmpty()) {
			// ctrl + q
			if (dragType == 1) {
				final ItemStack tossStack = handler.extractItem(slotIndex, slotStack.getMaxStackSize(), false);
				player.dropItem(tossStack, false);
				return slotStack;
			} else return super.slotClick(slotIndex, dragType, clickType, player);
		}

		if (clickType == ClickType.PICKUP) {
			final InventoryPlayer playerInventory = player.inventory;
			final ItemStack mouseStack = playerInventory.getItemStack();

			// Holding an item
			if (!mouseStack.isEmpty()) {
				// If we should try place the item into the container
				if (slotStack.isEmpty() && slot.isItemValid(mouseStack)) {
					int placeCount = dragType == 0 ? mouseStack.getCount() : 1;

					if (placeCount > slot.getItemStackLimit(mouseStack)) {
						placeCount = slot.getItemStackLimit(mouseStack);
					}

					slot.putStack(mouseStack.splitStack(placeCount));
					return ItemStack.EMPTY;
				}

				// If we should try to swap the stacks
				if (!ItemStack.areItemsEqual(slotStack, mouseStack)) {
					if (mouseStack.getCount() <= slot.getSlotStackLimit()) {
						slot.putStack(mouseStack);
						playerInventory.setItemStack(slotStack);
						return mouseStack;
					} else return slotStack;
				}

				// Stack is full
				if (slotStack.getCount() >= slot.getSlotStackLimit()) return slotStack;

				// If we should try to merge the stacks
				if (ItemStack.areItemsEqual(slotStack, mouseStack)) {
					if (slotStack.isStackable() && slotStack.getCount() <= slot.getSlotStackLimit()) {
						if (dragType == 0) {
							// This is a left click
							final ItemStack remainer = handler.insertItem(slotIndex, mouseStack, false);
							playerInventory.setItemStack(remainer);
						} else {
							// This is a rightclick
							slotStack.setCount(slotStack.getCount() + 1);
							mouseStack.setCount(mouseStack.getCount() - 1);
							playerInventory.setItemStack(mouseStack);
						}
						return slotStack;
					}
				}
			}
			//Stack size is within the normal bounds. Example: cobble = 64 or less
			if (slotStack.getCount() <= slotStack.getMaxStackSize()) return super.slotClick(slotIndex, dragType, clickType, player);

			// Grabbing items from sack
			if (mouseStack.isEmpty()) {
				int amount = slotStack.getMaxStackSize();
				if (dragType == 1) amount = amount / 2;
				final ItemStack extractedItem = handler.extractItem(slotIndex, amount, false);
				playerInventory.setItemStack(extractedItem);
				return ItemStack.EMPTY;
			}
		}

		return super.slotClick(slotIndex, dragType, clickType, player);
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		return true;
	}

	@Override
	protected final boolean mergeItemStack(final ItemStack mergeStack, final int startIndex, final int endIndex, final boolean reverseDirection) {
		boolean haveMergedStack = false;
		// Start iterating from the end if we merge in reverse order
		int slotIndex = reverseDirection ? endIndex - 1 : startIndex;

		if (mergeStack.isStackable()) {
			while (!mergeStack.isEmpty()) {
				if (reverseDirection) {
					if (slotIndex < startIndex) break;
				} else if (slotIndex >= endIndex) break;

				final Slot slot = getSlot(slotIndex);
				final ItemStack slotStack = slot.getStack();

				// Can't merge these stacks
				if (!mergeStack.isItemEqual(slotStack) || !ItemStack.areItemStackTagsEqual(mergeStack, slotStack)) {
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				final int total = slotStack.getCount() + mergeStack.getCount();

				final int maxSize;
				// If it's our container slots use the slot limit to determine the max stack size
				if (slotIndex < slotCount) {
					maxSize = slot.getSlotStackLimit();
				} else maxSize = Math.min(slot.getSlotStackLimit(), mergeStack.getMaxStackSize());

				// Can fully consume the merge stack
				if (maxSize >= total) {
					mergeStack.setCount(0);
					slotStack.setCount(total);
					slot.onSlotChanged();
					haveMergedStack = true;
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				// Can only partially consume the stack
				if (maxSize > slotStack.getCount()) {
					mergeStack.shrink(maxSize - slotStack.getCount());
					slotStack.grow(maxSize - slotStack.getCount());
					slot.onSlotChanged();
					haveMergedStack = true;
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}
				slotIndex += (reverseDirection) ? -1 : 1;
			}
		}

		// Try and fill empty slots now
		if (!mergeStack.isEmpty()) {
			if (reverseDirection) {
				slotIndex = endIndex - 1;
			} else slotIndex = startIndex;

			while (true) {
				if (reverseDirection) {
					if (slotIndex < startIndex) break;
				} else if (slotIndex >= endIndex) break;

				final Slot slot = getSlot(slotIndex);

				// Continue early if we can't put anything in this slot
				if (slot.getHasStack() || !slot.isItemValid(mergeStack)) {
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				// If it's our container slots use the slots stack cap
				if (slotIndex < slotCount) {
					slot.putStack(mergeStack.splitStack(slot.getSlotStackLimit()));
					haveMergedStack = true;
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				{
					final int splitSize = Math.min(slot.getItemStackLimit(mergeStack), mergeStack.getMaxStackSize());
					// Can merge
					if (mergeStack.getCount() > splitSize) {
						slot.putStack(mergeStack.splitStack(splitSize));
						haveMergedStack = true;
						slotIndex += (reverseDirection) ? -1 : 1;
						continue;
					}
				}

				// Put the whole stack in the slot
				slot.putStack(mergeStack.splitStack(mergeStack.getCount()));
				haveMergedStack = true;
				slotIndex += (reverseDirection) ? -1 : 1;
			}
		}
		return haveMergedStack;
	}

	/**
	 * Adds the slots for this container
	 */
	protected void addContainerSlots() {
		switch (handler.getSlots()) {
			case 1:
				// 1 slot container
				addSlots(1, 1, 80, 32);
				break;
			case 4:
				// 4 slot container
				addSlots(2, 2, 71, 23);
				break;
			case 8:
				// 8 slot container
				addSlots(2, 4, 53, 23);
				break;
			case 18:
				addSlots(2, 9, 8, 34);
				break;
			default:
				// We want to round up, integer math rounds down
				final int rows = (int) Math.ceil((double) handler.getSlots() / 9);
				final int columns = handler.getSlots() / rows;
				addSlots(rows, columns);
		}
	}

	/**
	 * Dynamically adds slots to the container depending on the amount of rows and columns.
	 *
	 * @param rows How many rows of slots
	 * @param columns How many columns of slots
	 * @param startX The X starting position
	 * @param startY The Y starting position
	 */
	private void addSlots(final int rows, final int columns, final int startX, final int startY) {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				addSlotToContainer(new SlotItemHandler(handler, column + row * columns, startX + column * 18, startY + row * 18));
			}
		}
	}

	/**
	 * Dynamically adds slots to the container depending on the amount of rows and columns.
	 * Will start from the top left
	 *
	 * @param rows How many rows of slots
	 * @param columns How many columns of slots
	 */
	private void addSlots(final int rows, final int columns) {
		for (int row = 0; row < rows; row++) {
			if (row == rows - 1) {
				for (int column = 0; column < columns; column++) {
					addSlotToContainer(new SlotItemHandler(handler, column + row * columns, 8 + column * 18, 27 + row * 18));
				}
			} else {
				for (int j = 0; j < 9; j++) {
					addSlotToContainer(new SlotItemHandler(handler, j + row * columns, 8 + j * 18, 27 + row * 18));
				}
			}
		}
	}

	/**
	 * Adds the player inventory slots
	 *
	 * @param inventoryPlayer The player inventory
	 */
	protected final void addPlayerInventorySlots(final InventoryPlayer inventoryPlayer) {
		// Add Player Inventory Slots
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		// Add player hotbar slots
		for (int k = 0; k < 9; k++) {
			addSlotToContainer(new Slot(inventoryPlayer, k, 8 + k * 18, 142));
		}
	}
}