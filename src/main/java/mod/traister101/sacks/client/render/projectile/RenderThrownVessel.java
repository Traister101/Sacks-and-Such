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

    private static final RenderItem ITEM_RENDERER = Minecraft.getMinecraft().getRenderItem();

    public RenderThrownVessel(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void doRender(@Nonnull EntityExplosiveVessel entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableLighting();

        final float horizontalAngle = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 180;

        if (entity.rotationPitch >= 45) {
            GlStateManager.translate(0, -0.05, 0);
        } else {
            GlStateManager.translate(0, 0.05, 0);
        }

        GlStateManager.translate(x, y, z);

        GlStateManager.rotate(horizontalAngle, 0, 1, 0);
        GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        ITEM_RENDERER.renderItem(getStackForType(entity.getType()), ItemCameraTransforms.TransformType.GROUND);

        GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull EntityExplosiveVessel entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    private ItemStack getStackForType(final VesselType type) {
        switch (type) {
            case EXPLOSIVE:
                return new ItemStack(ItemsSNS.EXPLOSIVE_VESSEL);
            case STICKY:
                return new ItemStack(ItemsSNS.STICKY_EXPLOSIVE_VESSEL);
            case TINY:
                return new ItemStack(ItemsSNS.TINY_EXPLOSIVE_VESSEL);
            default:
                return ItemStack.EMPTY;
        }
    }
}