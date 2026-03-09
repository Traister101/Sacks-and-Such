package mod.traister101.sns.client;

import mod.traister101.sns.client.utils.SNSRenderHelper;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.LunchboxTooltip;
import net.dries007.tfc.client.ClientDeviceImageTooltip;
import net.dries007.tfc.client.ClientDeviceImageTooltip.Texture;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.ItemStack;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientLunchboxTooltip implements ClientTooltipComponent {

	public static final int DEFAULT_COLOR = 0x1bab44;
	private final LunchboxTooltip tooltip;

	private static void blit(final GuiGraphics graphics, final int x, final int y, final Texture texture) {
		graphics.blit(ClientDeviceImageTooltip.TEXTURE_LOCATION, x, y, 0, (float) texture.x, (float) texture.y, texture.w, texture.h, 128, 128);
	}

	private static void drawBorder(final GuiGraphics graphics, final int x, final int y, final int width, final int height) {
		blit(graphics, x, y, Texture.BORDER_CORNER_TOP);
		blit(graphics, x + width * 18 + 1, y, Texture.BORDER_CORNER_TOP);

		for (int i = 0; i < width; ++i) {
			blit(graphics, x + 1 + i * 18, y, Texture.BORDER_HORIZONTAL_TOP);
			blit(graphics, x + 1 + i * 18, y + height * 20, Texture.BORDER_HORIZONTAL_BOTTOM);
		}

		for (int j = 0; j < height; ++j) {
			blit(graphics, x, y + j * 20 + 1, Texture.BORDER_VERTICAL);
			blit(graphics, x + width * 18 + 1, y + j * 20 + 1, Texture.BORDER_VERTICAL);
		}

		blit(graphics, x, y + height * 20, Texture.BORDER_CORNER_BOTTOM);
		blit(graphics, x + width * 18 + 1, y + height * 20, Texture.BORDER_CORNER_BOTTOM);
	}

	private static void renderStack(final Font font, final ItemStack itemstack, final int x, final int y, final GuiGraphics graphics) {
		graphics.renderItem(itemstack, x, y, x * y);
		graphics.renderItemDecorations(font, itemstack, x, y);
	}

	private static void renderSlot(final GuiGraphics graphics, final Font font, final int x, final int y, final ItemStack itemstack) {
		blit(graphics, x, y, Texture.SLOT);
		renderStack(font, itemstack, x + 1, y + 1, graphics);
	}

	private static int getColor() {
		final int color;
		try {
			color = Integer.parseInt(SNSConfig.CLIENT.lunchboxSelectedSlotHighlightColor.get(), 16);
		} catch (final NumberFormatException e) {
			final var red = ARGB32.red(DEFAULT_COLOR);
			final var green = ARGB32.green(DEFAULT_COLOR);
			final var blue = ARGB32.blue(DEFAULT_COLOR);
			return ARGB32.color(128, red, green, blue);
		}
		final var red = ARGB32.red(color);
		final var green = ARGB32.green(color);
		final var blue = ARGB32.blue(color);
		return ARGB32.color(128, red, green, blue);
	}

	@Override
	public int getHeight() {
		return tooltip.height() * 20 + 2 + 4;
	}

	@Override
	public int getWidth(final Font font) {
		return tooltip.width() * 18 + 2;
	}

	@Override
	public void renderImage(final Font font, final int mouseX, final int mouseY, final GuiGraphics graphics) {
		final int width = tooltip.width();
		final int height = tooltip.height();

		for (var y = 0; y < height; ++y) {
			for (var x = 0; x < width; ++x) {
				final int slotX = mouseX + x * 18 + 1;
				final int slotY = mouseY + y * 20 + 1;
				final var slotIndex = y * width + x;
				renderSlot(graphics, font, slotX, slotY, tooltip.items().get(slotIndex));
				if (tooltip.selectedSlot() == slotIndex) {
					final var color = getColor();
					SNSRenderHelper.hollowBox(graphics, slotX + 1, slotY + 1, 16, 16, color,
							SNSConfig.CLIENT.lunchboxSelectedSlotHighlightThickness.get());
				}
			}
		}

		drawBorder(graphics, mouseX, mouseY, width, height);
	}
}