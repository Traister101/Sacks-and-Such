package mod.traister101.sacks.client.render.projectile;

import mod.traister101.sacks.objects.entity.projectile.EntityExplosiveVessel;
import mod.traister101.sacks.objects.items.ItemsSNS;
import mod.traister101.sacks.util.VesselType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderThrownVessel extends Render<EntityExplosiveVessel> {

    private final RenderItem itemRenderer;

    public RenderThrownVessel(RenderManager renderManagerIn) {
        super(renderManagerIn);
        itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    public void doRender(@Nonnull EntityExplosiveVessel entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        bindTexture(getEntityTexture(entity));

        VesselType type = entity.getType();
        ItemStack stack = ItemStack.EMPTY;
        switch (type) {
            case EXPLOSIVE:
                stack = new ItemStack(ItemsSNS.EXPLOSIVE_VESSEL);
                break;
            case STICKY:
                stack = new ItemStack(ItemsSNS.STICKY_EXPLOSIVE_VESSEL);
                break;
            case TINY:
                stack = new ItemStack(ItemsSNS.TINY_EXPLOSIVE_VESSEL);
                break;
            default:
                break;
        }

        if (!stack.isEmpty())
            itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull EntityExplosiveVessel entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}