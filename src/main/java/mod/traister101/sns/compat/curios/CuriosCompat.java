package mod.traister101.sns.compat.curios;

import mod.traister101.sns.client.renderer.curios.*;
import mod.traister101.sns.common.items.*;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.item.ItemStack;

public final class CuriosCompat {

	public static void clientSetup() {
		CuriosApi.registerCurio(SNSItems.SNOW_SHOES.get(), getCurio(SNSItems.SNOW_SHOES.get()));
		CuriosApi.registerCurio(SNSItems.REINFORCED_SNOW_SHOES.get(), getCurio(SNSItems.REINFORCED_SNOW_SHOES.get()));
		CuriosRendererRegistry.register(SNSItems.FRAME_PACK.get(), FramePackCurioRenderer::new);
		{
			final var smallSackCurio = HipCurioRenderer.smallSackFactory().supplier();
			CuriosRendererRegistry.register(SNSItems.LEATHER_SACK.get(), smallSackCurio.create(HipCurioRenderer.LEATHER_SACK_TEXTURE));
			CuriosRendererRegistry.register(SNSItems.BURLAP_SACK.get(), smallSackCurio.create(HipCurioRenderer.BURLAP_SACK_TEXTURE));
			CuriosRendererRegistry.register(SNSItems.SEED_POUCH.get(), smallSackCurio.create(HipCurioRenderer.SEED_POUCH_TEXTURE));
		}
		{
			final var largeSackCurio = HipCurioRenderer.largeSackFactory().supplier();
			CuriosRendererRegistry.register(SNSItems.ORE_SACK.get(), largeSackCurio.create(HipCurioRenderer.ORE_SACK_TEXTURE));
		}
	}

	private static ICurioItem getCurio(final SnowShoesItem snowShoesItem) {
		return new ICurioItem() {
			@Override
			public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
				return true;
			}
		};
	}
}