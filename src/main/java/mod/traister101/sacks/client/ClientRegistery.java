package mod.traister101.sacks.client;

import mod.traister101.sacks.client.render.projectile.RenderThrownVessel;
import mod.traister101.sacks.objects.entity.projectile.EntityExplosiveVessel;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import mod.traister101.sacks.objects.items.ItemsSNS;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mod.traister101.sacks.SacksNSuch.MODID;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT, modid = MODID)
public final class ClientRegistery {

    public static void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityExplosiveVessel.class, RenderThrownVessel::new);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // ITEMS //
        for (ItemSack sack : ItemsSNS.getAllSacks()) registerBasicItemRenderer(sack);

        for (ItemThrowableVessel vessel : ItemsSNS.getAllThrowableVessels()) registerBasicItemRenderer(vessel);

        for (Item item : ItemsSNS.getAllSimpleItems()) registerBasicItemRenderer(item);
    }

    private static void registerBasicItemRenderer(Item item) {
        registerItemRenderer(item, 0, "inventory");
    }

    private static void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }
}