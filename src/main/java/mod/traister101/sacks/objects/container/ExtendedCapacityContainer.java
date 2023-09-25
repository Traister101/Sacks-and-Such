package mod.traister101.sacks.objects.container;

import mod.traister101.sacks.objects.inventory.capability.ExtendedSlotCapacityHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class ExtendedCapacityContainer extends Container {

	protected final ExtendedSlotCapacityHandler handler;
	protected final EntityPlayer player;
	protected final int slotCount;
	protected int slotStackCap;
	/** Index of the item container, so we can prevent moving it */
	protected int heldItemIndex;

	protected ExtendedCapacityContainer(final InventoryPlayer inventoryPlayer, final ItemStack heldStack) {
		this.player = inventoryPlayer.player;

		{
			if (!heldStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
				throw new IllegalArgumentException("The given ItemStack requires Item handling capability");

			final IItemHandler itemHandler = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

			if (!(itemHandler instanceof ExtendedSlotCapacityHandler))
				throw new IllegalArgumentException("The capability MUST BE or extend ExtendedSlotCapacityHandler");

			this.handler = (ExtendedSlotCapacityHandler) itemHandler;
		}

		this.slotCount = handler.getSlots();
		this.slotStackCap = handler.getSlotStackLimit();

		// Initalize the heldItemIndex
		if (heldStack == player.getHeldItemMainhand()) {
			// Mainhand opened inventory
			this.heldItemIndex = inventoryPlayer.currentItem + 27;
		} else {
			// Offhand, so ignore this rule as we can't click it
			this.heldItemIndex = -100;
		}
		this.heldItemIndex += slotCount;

		addContainerSlots();
		addPlayerInventorySlots(inventoryPlayer);
	}

	@Override
	public final ItemStack transferStackInSlot(final EntityPlayer player, final int slotIdex) {
		final Slot slot = inventorySlots.get(slotIdex);

		if (slot.getHasStack()) {
			final ItemStack slotStack = slot.getStack();

			if (slotCount > slotIdex) {
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
	public final ItemStack slotClick(final int slotID, final int dragType, final ClickType clickType, final EntityPlayer player) {
		// Not a slot, let vanilla handle it
		if (0 > slotID) return super.slotClick(slotID, dragType, clickType, player);

		// Prevent moving of the item stack that is currently open
		if (heldItemIndex == slotID) return ItemStack.EMPTY;

		// Keybind swap
		if (clickType == ClickType.SWAP && 0 <= dragType && 9 > dragType) {
			final ItemStack hoverStack = inventorySlots.get(slotID).getStack();
			final ItemStack hotbarStack = player.inventory.getStackInSlot(dragType);
			// Blocks the held stack from being moved
			if (hoverStack == player.getHeldItemMainhand() || player.getHeldItemMainhand() == hotbarStack) return ItemStack.EMPTY;

			// If either stack is empty pass it to vanilla
			if (hotbarStack.isEmpty() || hoverStack.isEmpty()) return super.slotClick(slotID, dragType, clickType, player);

			// If neither stack is too large pass to vanilla
			if (hotbarStack.getCount() <= hotbarStack.getMaxStackSize() && hoverStack.getCount() <= hoverStack.getMaxStackSize())
				return super.slotClick(slotID, dragType, clickType, player);

			return ItemStack.EMPTY;
		}

		// Vanilla slot, pass to vanilla
		if (slotCount - 1 < slotID) return super.slotClick(slotID, dragType, clickType, player);

		final Slot slot = getSlot(slotID);

		// Shift click
		if (clickType == ClickType.QUICK_MOVE) {
			if (!slot.canTakeStack(player)) return ItemStack.EMPTY;

			return transferStackInSlot(player, slotID);
		}

		final ItemStack slotStack = slot.getStack();
		// Slot is empty give to vanilla
		if (slotStack.isEmpty()) return super.slotClick(slotID, dragType, clickType, player);

		// Pressing q
		if (clickType == ClickType.THROW && player.inventory.getItemStack().isEmpty()) {
			// ctrl + q
			if (dragType == 1) {
				final ItemStack tossStack = handler.extractItem(slotID, slotStack.getMaxStackSize(), false);
				player.dropItem(tossStack, false);
				return slotStack;
			} else return super.slotClick(slotID, dragType, clickType, player);
		}

		if (clickType == ClickType.PICKUP) {
			final InventoryPlayer playerInventory = player.inventory;
			final ItemStack mouseStack = playerInventory.getItemStack();

			// Holding an item
			if (!mouseStack.isEmpty()) {
				// If we should try to swap the stacks
				if (!ItemStack.areItemsEqual(slotStack, mouseStack)) {
					if (isStacksSwappable(slotStack, mouseStack, slotID)) {
						slot.putStack(mouseStack);
						playerInventory.setItemStack(slotStack);
						return mouseStack;
					} else {
						return slotStack;
					}
				}

				// Stack is full
				if (slotStack.getCount() >= slot.getSlotStackLimit()) return slotStack;

				// If we should try to merge the stacks
				if (ItemStack.areItemsEqual(slotStack, mouseStack)) {
					if (slotStack.isStackable() && slotStack.getCount() <= slotStackCap) {
						if (dragType == 0) {
							// This is a left click
							final ItemStack remainer = handler.insertItem(slotID, mouseStack, false);
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
			if (slotStack.getCount() <= slotStack.getMaxStackSize()) return super.slotClick(slotID, dragType, clickType, player);

			// Grabbing items from sack
			if (mouseStack.isEmpty()) {
				int amount = slotStack.getMaxStackSize();
				if (dragType == 1) amount = amount / 2;
				final ItemStack extractedItem = handler.extractItem(slotID, amount, false);
				playerInventory.setItemStack(extractedItem);
				return ItemStack.EMPTY;
			}
		}
		return super.slotClick(slotID, dragType, clickType, player);
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		return true;
	}

	@Override
	protected final boolean mergeItemStack(final ItemStack stackIn, final int startIndex, final int endIndex, final boolean reverseDirection) {
		boolean mergedStack = false;
		int slotIndex = startIndex;

		if (reverseDirection) slotIndex = endIndex - 1;

		while (!stackIn.isEmpty()) {
			if (reverseDirection) {
				if (slotIndex < startIndex) break;
			} else if (slotIndex >= endIndex) break;

			final Slot slot = getSlot(slotIndex);
			final ItemStack slotStack = slot.getStack();

			if (!slotStack.isEmpty() && slotStack.getItem() == stackIn.getItem() && (stackIn.getMetadata() == slotStack.getMetadata()) &&
					ItemStack.areItemStackTagsEqual(stackIn, slotStack)) {

				if (slotCount > slotIndex) {
					final ItemStack remainer = handler.insertItem(slotIndex, stackIn, false);
					stackIn.setCount(remainer.getCount());
				} else {
					final int total = slotStack.getCount() + stackIn.getCount();
					final int maxSize = Math.min(slot.getSlotStackLimit(), stackIn.getMaxStackSize());
					if (total <= maxSize) {
						stackIn.setCount(0);
						slotStack.setCount(total);
					} else if (slotStack.getCount() < maxSize) {
						stackIn.shrink(maxSize - slotStack.getCount());
						slotStack.setCount(slotStack.getMaxStackSize());
					}
					slot.onSlotChanged();
					mergedStack = true;
				}
			}
			slotIndex += (reverseDirection) ? -1 : 1;
		}

		if (!stackIn.isEmpty()) {
			if (reverseDirection) {
				slotIndex = endIndex - 1;
			} else slotIndex = startIndex;

			while (true) {
				if (reverseDirection) {
					if (slotIndex < startIndex) break;
				} else if (slotIndex >= endIndex) break;

				final Slot slot = getSlot(slotIndex);
				final ItemStack slotStack = slot.getStack();

				if (slotStack.isEmpty() && slot.isItemValid(stackIn)) {
					if (slotCount > slotIndex) {
						slot.putStack(stackIn.splitStack(slotStackCap));
					} else if (stackIn.getCount() > stackIn.getMaxStackSize()) {
						slot.putStack(stackIn.splitStack(stackIn.getMaxStackSize()));
					} else {
						slot.putStack(stackIn.splitStack(stackIn.getCount()));
					}
					slot.onSlotChanged();
					mergedStack = true;
				}
				slotIndex += (reverseDirection) ? -1 : 1;
			}
		}
		return mergedStack;
	}

	/**
	 * Checks if the two stacks are swappable
	 *
	 * @param stackA Stack A to compare
	 * @param stackB Stack B to compare
	 * @param slotIndex SlotIndex to check if Stack B is valid
	 *
	 * @return If the stacks are swapabled
	 */
	private boolean isStacksSwappable(final ItemStack stackA, final ItemStack stackB, final int slotIndex) {
		final int countA = stackA.getCount();
		final int countB = stackB.getCount();

		boolean swappableA = false;
		boolean swappableB = false;

		if (countA <= slotStackCap && countA <= stackA.getMaxStackSize()) swappableA = true;

		if (countB <= slotStackCap && countB <= stackB.getMaxStackSize() && handler.isItemValid(slotIndex, stackB)) swappableB = true;

		return swappableA && swappableB;
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