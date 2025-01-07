package mod.traister101.sns.client.renderer.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.client.models.*;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

/**
 * This is unnecessarily complicated and harder to maintain, but I thought it was fun so we keeping it.
 */
public final class HipCurioRenderer<M extends Model> implements ICurioRenderer {

	public static final ResourceLocation LEATHER_SACK_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/leather_sack.png");
	public static final ResourceLocation BURLAP_SACK_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/burlap_sack.png");
	public static final ResourceLocation SEED_POUCH_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/seed_pouch.png");
	public static final ResourceLocation ORE_SACK_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/ore_sack.png");

	private final ResourceLocation texture;
	private final M model;
	@Nullable
	private final ModelAnimator<M> modelAnimator;

	private HipCurioRenderer(final ResourceLocation texture, final M model, final @Nullable ModelAnimator<M> modelAnimator) {
		this.texture = texture;
		this.model = model;
		this.modelAnimator = modelAnimator;
	}

	private static ModelPart bakeLayer(final ModelLayerLocation layerLocation) {
		final EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		return entityModels.bakeLayer(layerLocation);
	}

	public static <M extends Model> CurioRendererFactory factory(final ModelFactory<M> modelFactory, final ModelLayerLocation layerLocation,
			final @Nullable ModelAnimator<M> setupAnim) {
		return texture -> new HipCurioRenderer<>(texture, modelFactory.create(HipCurioRenderer.bakeLayer(layerLocation)), setupAnim);
	}

	public static CurioRendererFactory largeSackFactory() {
		return HipCurioRenderer.factory(LargeSackModel::new, LargeSackModel.LAYER_LOCATION, LargeSackModel::setupAnim);
	}

	public static CurioRendererFactory smallSackFactory() {
		return HipCurioRenderer.factory(SmallSackModel::new, SmallSackModel.LAYER_LOCATION, SmallSackModel::setupAnim);
	}

	@Override
	public <E extends LivingEntity, EM extends EntityModel<E>> void render(final ItemStack itemStack, final SlotContext slotContext,
			final PoseStack poseStack, final RenderLayerParent<E, EM> renderLayerParent, final MultiBufferSource bufferSource, final int packedLight,
			final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw,
			final float headPitch) {
		if (itemStack.isEmpty()) return;

		final LivingEntity entity = slotContext.entity();
		poseStack.pushPose();

		poseStack.mulPose(Axis.YP.rotationDegrees((slotContext.index() % 2) == 1 ? -90 : 90));
		poseStack.scale(0.75F, 0.75F, 0.75F);
		poseStack.translate(0, -0.15, 0.8);

		if (entity.isCrouching()) {
			poseStack.translate(-0.35, 0.1875F, 0);
		}

		if (modelAnimator != null) modelAnimator.animate(model, limbSwing, limbSwingAmount);
		model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(texture)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

		poseStack.popPose();
	}

	@FunctionalInterface
	public interface CurioRendererFactory {

		ICurioRenderer create(ResourceLocation texture);

		default CurioRendererSupplierFactory supplier() {
			return texture -> () -> create(texture);
		}

		@FunctionalInterface
		interface CurioRendererSupplierFactory {

			Supplier<ICurioRenderer> create(ResourceLocation texture);
		}
	}

	@FunctionalInterface
	public interface ModelFactory<M extends Model> {

		M create(ModelPart root);
	}

	@FunctionalInterface
	public interface ModelAnimator<M extends Model> {

		void animate(M model, float limbSwing, float limbSwingAmount);
	}
}