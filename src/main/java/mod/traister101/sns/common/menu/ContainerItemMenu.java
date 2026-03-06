package mod.traister101.sns.common.menu;

import com.google.common.base.Supplier;
import mod.traister101.esc.common.menu.ExtendedSlotCapacityMenu;
import mod.traister101.esc.common.slot.ExtendedSlotItemHandler;
import mod.traister101.sns.util.ItemSlotData.*;

import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

public class ContainerItemMenu extends ExtendedSlotCapacityMenu {

	public static final int OFFHAND_MAGIC_INDEX = -1;
	private static final Set<ClickType> ILLEGAL_ITEM_CLICKS = EnumSet.of(ClickType.QUICK_MOVE, ClickType.PICKUP, ClickType.THROW, ClickType.SWAP);
	/**
	 * The stack supplier for this menus container item stack
	 */
	public final Supplier<ItemStack> containerStackSupplier;
	/**
	 * Index in the hotbar. Between [0, 9), or -1 if this is the offhand
	 */
	protected final int hotbarIndex;
	/**
	 * Index into the slot for the hotbar slot. Hotbar is at the end of the inventory.
	 */
	protected int containerItemIndex;

	private ContainerItemMenu(final int windowId, final int containerSlots, final int containerItemIndex, final int hotbarIndex,
			final Supplier<ItemStack> containerStackSupplier) {
		super(SNSMenus.CONTAINER_ITEM_MENU.get(), windowId, containerSlots);
		this.containerItemIndex = containerItemIndex;
		this.hotbarIndex = hotbarIndex;
		this.containerStackSupplier = containerStackSupplier;
	}

	public static ContainerItemMenu forHeld(final int windowId, final Inventory inventory, final HeldSlotData heldSlotData) {
		final var handler = heldSlotData.stack(inventory.player).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();
		final int hotbarIndex, containerItemIndex;
		if (heldSlotData.mainHand()) {
			hotbarIndex = inventory.selected;
			containerItemIndex = handler.getSlots() + inventory.selected + 27;
		} else {
			hotbarIndex = OFFHAND_MAGIC_INDEX;
			containerItemIndex = Integer.MIN_VALUE;
		}

		final var containerItemMenu = new ContainerItemMenu(windowId, handler.getSlots(), containerItemIndex, hotbarIndex,
				() -> heldSlotData.stack(inventory.player));
		containerItemMenu.addContainerSlots(handler);
		containerItemMenu.addPlayerInventorySlots(inventory);
		return containerItemMenu;
	}

	public static ContainerItemMenu forInventory(final int windowId, final Inventory inventory, final InventorySlotData inventorySlotData) {
		final var handler = inventorySlotData.stack(inventory.player).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();
		final var slotCount = handler.getSlots();
		final var containerItemMenu = new ContainerItemMenu(windowId, slotCount,
				slotCount + inventorySlotData.slotIndex() - Inventory.getSelectionSize(), Integer.MIN_VALUE,
				() -> inventorySlotData.stack(inventory.player));
		containerItemMenu.addContainerSlots(handler);
		containerItemMenu.addPlayerInventorySlots(inventory);
		return containerItemMenu;
	}

	public static ContainerItemMenu forCurios(final int windowId, final Inventory inventory, final CuriosSlotData curiosSlotData) {
		final var handler = curiosSlotData.stack(inventory.player).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();
		final var slotCount = handler.getSlots();
		final var containerItemMenu = new ContainerItemMenu(windowId, slotCount, Integer.MIN_VALUE, Integer.MIN_VALUE,
				() -> curiosSlotData.stack(inventory.player));
		containerItemMenu.addContainerSlots(handler);
		containerItemMenu.addPlayerInventorySlots(inventory);
		return containerItemMenu;
	}

	@Override
	public void clicked(final int slotIndex, final int mouseButton, final ClickType clickType, final Player player) {
		// We can't move if:
		// the slot is the item index, and it's an illegal action (like, swapping the items)
		// the hotbar item is being swapped out
		// the action is "pickup all" (this ignores every slot, so we cannot allow it)
		if (slotIndex == containerItemIndex && ILLEGAL_ITEM_CLICKS.contains(clickType)) return;
		if (mouseButton == hotbarIndex && clickType == ClickType.SWAP) return;
		if (mouseButton == Inventory.SLOT_OFFHAND && clickType == ClickType.SWAP && hotbarIndex == OFFHAND_MAGIC_INDEX) return;

		super.clicked(slotIndex, mouseButton, clickType, player);
	}

	@Override
	public boolean stillValid(final Player player) {
		return !getContainerStack().isEmpty();
	}

	public final ItemStack getContainerStack() {
		return containerStackSupplier.get();
	}

	/**
	 * Adds the slots for this container
	 */
	protected void addContainerSlots(final IItemHandler handler) {
		switch (containerSlots) {
			case 1 -> addSlots(handler, 1, 1, 80, 32);
			case 4 -> addSlots(handler, 2, 2, 71, 23);
			case 8 -> addSlots(handler, 2, 4, 53, 23);
			case 18 -> addSlots(handler, 2, 9, 8, 23);
			default -> {
				// We want to round up, integer math rounds down
				final int rows = Math.round((float) containerSlots / 9);
				final int columns = containerSlots / rows;
				addSlots(handler, rows, columns);
			}
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
	private void addSlots(final IItemHandler handler, final int rows, final int columns, final int startX, final int startY) {
		assert rows != 0 : "Cannot have zero rows of slots";
		assert columns != 0 : "Cannot have zero columns of slots";

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				final int yPosition = startY + row * 18;
				final int xPosition = startX + column * 18;
				final int index = column + row * columns;
				addSlot(new ExtendedSlotItemHandler(handler, index, xPosition, yPosition));
			}
		}
	}

	/**
	 * Dynamically adds slots to the container depending on the amount of rows and columns. Will start from the top left
	 *
	 * @param rows How many rows of slots
	 * @param columns How many columns of slots
	 */
	private void addSlots(final IItemHandler handler, final int rows, final int columns) {
		if (rows > 1) {
			addSlots(handler, rows - 1, 9, 8, 18);
		}

		for (int column = 0; column < columns; column++) {
			final int yPosition = 18 * (rows - 1) + 18;
			final int xPosition = 8 + column * 18;
			final int index = column + (rows - 1) * columns;
			addSlot(new ExtendedSlotItemHandler(handler, index, xPosition, yPosition));
		}
	}

	/**
	 * Adds the player inventory slots to the container.
	 */
	protected final void addPlayerInventorySlots(final Inventory inventory) {
		// Main Inventory. Indexes [0, 27)
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// Hotbar. Indexes [27, 36)
		for (int k = 0; k < 9; k++) {
			addSlot(new Slot(inventory, k, 8 + k * 18, 142));
		}
	}
}