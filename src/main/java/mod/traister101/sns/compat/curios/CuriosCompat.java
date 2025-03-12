package mod.traister101.sns.compat.curios;

import mod.traister101.sns.client.renderer.curios.*;
import mod.traister101.sns.common.items.SNSItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public final class CuriosCompat {

	public static void clientSetup() {
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
}