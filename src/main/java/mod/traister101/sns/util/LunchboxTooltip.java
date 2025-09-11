package mod.traister101.sns.util;

import mod.traister101.sns.util.items.*;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import org.jetbrains.annotations.Range;
import java.util.*;

public record LunchboxTooltip(List<ItemStack> items, int width, int height, int selectedSlot) implements TooltipComponent {

	public static Optional<TooltipComponent> getTooltipImage(final IItemHandler handler, @Range(from = 0, to = Integer.MAX_VALUE) final int width,
			@Range(from = 0, to = Integer.MAX_VALUE) final int height, @Range(from = 0, to = Integer.MAX_VALUE) final int selectedSlot) {
		final var stacks = ItemSlot.stream(handler).map(ItemHandlerSlot::getStack).toList();
		if (stacks.stream().allMatch(ItemStack::isEmpty)) {
			return Optional.empty();
		}

		assert width * height == stacks.size() : "Width: " + width + " * Height: " + height + "must be the same as the amount of slots: " + (stacks.size());
		assert selectedSlot < stacks.size() - 1 : "Selected slot: " + selectedSlot + " must be within the slot bounds [0," + (stacks.size() - 1) + ")";

		return Optional.of(new LunchboxTooltip(stacks, width, height, selectedSlot));
	}
}