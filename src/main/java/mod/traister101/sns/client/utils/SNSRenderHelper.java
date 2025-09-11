package mod.traister101.sns.client.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public final class SNSRenderHelper {

	/**
	 * @param x The x
	 * @param y The y
	 * @param width The box width
	 * @param height The box height
	 * @param color The color
	 * @param lineThickness The line thickness
	 */
	public static void hollowBox(final GuiGraphics graphics, final int x, final int y, final int width, final int height, final int color,
			final int lineThickness) {
		// Top
		graphics.fillGradient(RenderType.guiOverlay(), x, y, x + width, y + lineThickness, color, color, 0);
		// Left
		graphics.fillGradient(RenderType.guiOverlay(), x, y + lineThickness, x + lineThickness, y + height - lineThickness, color, color, 0);
		// Bottom
		graphics.fillGradient(RenderType.guiOverlay(), x, y + height - lineThickness, x + width, y + height, color, color, 0);
		// Right
		graphics.fillGradient(RenderType.guiOverlay(), x + width - lineThickness, y + lineThickness, x + width, y + height - lineThickness, color,
				color, 0);
	}
}