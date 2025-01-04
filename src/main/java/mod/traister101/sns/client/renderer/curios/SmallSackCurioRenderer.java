package mod.traister101.sns.client.renderer.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.client.models.SmallSackModel;
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

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class SmallSackCurioRenderer implements ICurioRenderer {

	public static final ResourceLocation LEATHER_SACK_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/leather_sack.png");
	public static final ResourceLocation BURLAP_SACK_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/burlap_sack.png");
	public static final ResourceLocation SEED_POUCH_TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/curios/seed_pouch.png");

	private final ResourceLocation texture;
	private final SmallSackModel model;

	public SmallSackCurioRenderer(final ResourceLocation texture) {
		this.texture = texture;
		final EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		this.model = new SmallSackModel(entityModels.bakeLayer(SmallSackModel.LAYER_LOCATION));
	}

	public static Supplier<ICurioRenderer> create(final ResourceLocation resourceLocation) {
		return () -> new SmallSackCurioRenderer(resourceLocation);
	}

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(final ItemStack itemStack, final SlotContext slotContext,
			final PoseStack poseStack, final RenderLayerParent<T, M> renderLayerParent, final MultiBufferSource bufferSource, final int packedLight,
			final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw,
			final float headPitch) {
		if (itemStack.isEmpty()) return;

		final LivingEntity entity = slotContext.entity();
		poseStack.pushPose();

		final boolean oddSlot = (slotContext.index() % 2) == 1;
		poseStack.mulPose(Axis.YP.rotationDegrees(oddSlot ? 90 : -90));

		poseStack.scale(0.75F, 0.75F, 0.75F);
		poseStack.translate(0, -0.15, 0.8);
		if (entity.isCrouching()) {
			poseStack.translate(-0.35, 0.1875F, 0);
		}

		model.setupAnim(limbSwing, limbSwingAmount);
		model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(texture)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

		poseStack.popPose();
	}
}