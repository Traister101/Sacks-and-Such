package mod.traister101.sns.compat.patchouli.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.util.Helpers;
import org.slf4j.Logger;
import vazkii.patchouli.api.*;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * TFCs {@link net.dries007.tfc.compat.patchouli.component.AnvilComponent} that respects the component x & y fields
 * TODO remove once TFC has a release with https://github.com/TerraFirmaCraft/TerraFirmaCraft/pull/2805
 */
@SuppressWarnings("unused")
public class AnvilComponent implements ICustomComponent {

	private static final Logger LOGGER = LogUtils.getLogger();
	protected transient @Nullable AnvilRecipe recipe;
	protected transient int x;
	protected transient int y;
	@SerializedName("recipe")
	String recipeName;

	protected static Optional<ResourceLocation> asResourceLocation(final String variable) {
		try {
			return Optional.of(Helpers.resourceLocation(variable));
		} catch (ResourceLocationException e) {
			LOGGER.error(e.getMessage());
			return Optional.empty();
		}
	}

	@SuppressWarnings({"unchecked", "deprecation"})
	protected static <T extends Recipe<?>> Optional<T> asRecipe(final String variable, final RecipeType<T> type) {
		return asResourceLocation(variable).flatMap(e -> ClientHelpers.getLevelOrThrow().getRecipeManager().byKey(e).flatMap(recipe -> {
			if (recipe.getType() != type) {
				LOGGER.error("The recipe {} of type {} is not of type {}", e, BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()), type);
				return Optional.empty();
			}
			return Optional.of((T) recipe);
		}).or(() -> {
			LOGGER.error("No recipe of type {} named {} ", BuiltInRegistries.RECIPE_TYPE.getKey(type), e);
			return Optional.empty();
		}));
	}

	@Override
	public void build(final int componentX, final int componentY, final int pageNum) {
		this.x = componentX;
		this.y = componentY;
		this.recipe = asRecipe(recipeName, TFCRecipeTypes.ANVIL.get()).orElse(null);
	}

	@Override
	public void render(final GuiGraphics graphics, final IComponentRenderContext context, final float pticks, final int mouseX, final int mouseY) {
		if (recipe == null) return;

		graphics.pose().pushPose();
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, PatchouliIntegration.TEXTURE);
		graphics.blit(PatchouliIntegration.TEXTURE, 9 + x, y, 0, 90, 98, 26, 256, 256);
		context.renderIngredient(graphics, 14 + x, 5 + y, mouseX, mouseY, recipe.getInput());
		context.renderItemStack(graphics, 86 + x, 5 + y, mouseX, mouseY, recipe.getResultItem(null));
		graphics.pose().popPose();
	}

	@Override
	public void onVariablesAvailable(final UnaryOperator<IVariable> lookup) {
		this.recipeName = lookup.apply(IVariable.wrap(recipeName)).asString();
	}
}