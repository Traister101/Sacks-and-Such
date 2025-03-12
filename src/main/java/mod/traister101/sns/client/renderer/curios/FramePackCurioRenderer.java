package mod.traister101.sns.client.renderer.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.client.models.FramePackModel;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class FramePackCurioRenderer implements ICurioRenderer {

	public static final ResourceLocation PACK_FRAME_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/frame_pack.png");
	private final FramePackModel model;

	public FramePackCurioRenderer() {
		final EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		this.model = new FramePackModel(entityModels.bakeLayer(FramePackModel.LAYER_LOCATION));
	}

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(final ItemStack itemStack, final SlotContext slotContext,
			final PoseStack poseStack, final RenderLayerParent<T, M> renderLayerParent, final MultiBufferSource bufferSource, final int packedLight,
			final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw,
			final float headPitch) {
		if (itemStack.isEmpty()) return;

		final LivingEntity entity = slotContext.entity();
		poseStack.pushPose();

		ICurioRenderer.translateIfSneaking(poseStack, entity);
		ICurioRenderer.rotateIfSneaking(poseStack, entity);

		poseStack.translate(0, -0.9, 0.6);

		model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(PACK_FRAME_TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1,
				1);

		poseStack.popPose();
	}
}