package mod.traister101.sns.common.menu;

import com.google.common.base.Supplier;
import mod.traister101.sns.common.items.SNSItems;
import mod.trasiter101.esc.common.menu.ExtendedSlotCapacityMenu;
import mod.trasiter101.esc.common.slot.ExtendedSlotItemHandler;
import top.theillusivec4.curios.api.*;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.function.Consumer;

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

	private ContainerItemMenu(final int windowId, final Inventory inventory, final IItemHandler handler, final InteractionHand hand) {
		super(SNSMenus.CONTAINER_ITEM_MENU.get(), windowId, handler.getSlots());

		if (hand == InteractionHand.MAIN_HAND) {
			this.hotbarIndex = inventory.selected;
			this.containerItemIndex = containerSlots + inventory.selected + 27;
		} else {
			this.hotbarIndex = OFFHAND_MAGIC_INDEX;
			this.containerItemIndex = Integer.MIN_VALUE;
		}

		this.containerStackSupplier = () -> hand == InteractionHand.MAIN_HAND ?
				slots.get(containerItemIndex).getItem() :
				inventory.player.getOffhandItem();

		this.addContainerSlots(handler);
		this.addPlayerInventorySlots(inventory);
	}

	private ContainerItemMenu(final int windowId, final Inventory inventory, final IItemHandler handler, final int inventorySlotIndex) {
		super(SNSMenus.CONTAINER_ITEM_MENU.get(), windowId, handler.getSlots());
		this.hotbarIndex = Integer.MIN_VALUE;
		this.containerItemIndex = containerSlots + inventorySlotIndex - 9;
		this.containerStackSupplier = () -> inventory.getItem(inventorySlotIndex);

		this.addContainerSlots(handler);
		this.addPlayerInventorySlots(inventory);
	}

	private ContainerItemMenu(final int windowId, final Inventory inventory, final IItemHandler handler,
			final Supplier<ItemStack> containerStackSupplier) {
		super(SNSMenus.CONTAINER_ITEM_MENU.get(), windowId, handler.getSlots());
		this.hotbarIndex = Integer.MIN_VALUE;
		this.containerItemIndex = Integer.MIN_VALUE;
		this.containerStackSupplier = containerStackSupplier;

		this.addContainerSlots(handler);
		this.addPlayerInventorySlots(inventory);
	}

	public static Consumer<FriendlyByteBuf> writeCurios(final String identifier, final int index) {
		return friendlyByteBuf -> {
			friendlyByteBuf.writeEnum(Type.WORN);
			friendlyByteBuf.writeUtf(identifier);
			friendlyByteBuf.writeVarInt(index);
		};
	}

	public static Consumer<FriendlyByteBuf> writeInventory(final int slotIndex) {
		return friendlyByteBuf -> {
			friendlyByteBuf.writeEnum(Type.INVENTORY);
			friendlyByteBuf.writeVarInt(slotIndex);
		};
	}

	public static Consumer<FriendlyByteBuf> writeHeld(final InteractionHand hand) {
		return friendlyByteBuf -> {
			friendlyByteBuf.writeEnum(Type.HELD);
			friendlyByteBuf.writeBoolean(hand == InteractionHand.MAIN_HAND);
		};
	}

	static ContainerItemMenu fromNetwork(final int windowId, final Inventory inventory, final FriendlyByteBuf byteBuf) {
		return switch (byteBuf.readEnum(Type.class)) {
			case HELD -> {
				final InteractionHand hand = byteBuf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

				final ItemStack heldStack = inventory.player.getItemInHand(hand);
				final IItemHandler itemHandler = heldStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();

				yield forHeld(windowId, inventory, itemHandler, hand);
			}

			case INVENTORY -> {
				final int slotIndex = byteBuf.readVarInt();
				final IItemHandler itemHandler = inventory.getItem(slotIndex).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();

				yield forInventory(windowId, inventory, itemHandler, slotIndex);
			}

			case WORN -> {
				final var curiosItemHandler = CuriosApi.getCuriosInventory(inventory.player).resolve().orElseThrow();

				final SlotResult slotResult = curiosItemHandler.findFirstCurio(SNSItems.FRAME_PACK.get()).orElseThrow();
				final ItemStack stack = slotResult.stack();

				final var itemHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();

				yield forUnIndexableStack(windowId, inventory, itemHandler, slotResult::stack);
			}
		};
	}

	public static ContainerItemMenu forHeld(final int windowId, final Inventory inventory, final IItemHandler handler, final InteractionHand hand) {
		return new ContainerItemMenu(windowId, inventory, handler, hand);
	}

	public static ContainerItemMenu forInventory(final int windowId, final Inventory inventory, final IItemHandler handler,
			final int inventorySlotIndex) {
		return new ContainerItemMenu(windowId, inventory, handler, inventorySlotIndex);
	}

	public static ContainerItemMenu forUnIndexableStack(final int windowId, final Inventory inventory, final IItemHandler handler,
			final Supplier<ItemStack> containerStackSupplier) {
		return new ContainerItemMenu(windowId, inventory, handler, containerStackSupplier);
	}

	public int getContainerSlots() {
		return containerSlots;
	}

	@Override
	public ItemStack quickMoveStack(final Player player, final int slotIndex) {
		final Slot slot = slots.get(slotIndex);

		if (slot.hasItem()) {
			final ItemStack slotStack = slot.getItem();

			if (slotIndex < containerSlots) {
				if (!moveItemStackTo(slotStack, containerSlots, slots.size(), true)) return ItemStack.EMPTY;
			} else if (!moveItemStackTo(slotStack, 0, containerSlots, false)) return ItemStack.EMPTY;

			if (slotStack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else slot.setChanged();

			return slotStack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void clicked(final int slotIndex, final int mouseButtom, final ClickType clickType, final Player player) {
		// We can't move if:
		// the slot is the item index, and it's an illegal action (like, swapping the items)
		// the hotbar item is being swapped out
		// the action is "pickup all" (this ignores every slot, so we cannot allow it)
		if (slotIndex == containerItemIndex && ILLEGAL_ITEM_CLICKS.contains(clickType)) return;
		if (mouseButtom == hotbarIndex && clickType == ClickType.SWAP) return;
		if (mouseButtom == Inventory.SLOT_OFFHAND && clickType == ClickType.SWAP && hotbarIndex == OFFHAND_MAGIC_INDEX) return;

		super.clicked(slotIndex, mouseButtom, clickType, player);
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

	enum Type {
		HELD,
		INVENTORY,
		WORN
	}
}