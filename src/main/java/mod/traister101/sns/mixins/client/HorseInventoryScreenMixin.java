package mod.traister101.sns.mixins.client;

import mod.traister101.sns.SacksNSuch;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;

@Mixin(HorseInventoryScreen.class)
public abstract class HorseInventoryScreenMixin extends AbstractContainerScreen<HorseInventoryMenu> {

	@Unique
	@SuppressWarnings("AddedMixinMembersNamePattern")
	private static final ResourceLocation TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/gui/horshoeslot.png");
	@Shadow
	@Final
	private AbstractHorse horse;

	public HorseInventoryScreenMixin(final HorseInventoryMenu pMenu, final Inventory pPlayerInventory, final Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	/**
	 * @reason We don't want to mess with the actual horse inventory screen texture so we draw the slot from its own texture
	 * @author Traister101
	 */
	@Inject(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIFFLnet/minecraft/world/entity/LivingEntity;)V"))
	public void sns$drawHorseshoesSlot(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY,
			final CallbackInfo ci) {
		final int slotX = (this.width - this.imageWidth) / 2;
		final int slotY = (this.height - this.imageHeight) / 2;

		guiGraphics.blit(TEXTURE, slotX + 7, slotY + (horse.canWearArmor() ? 53 : 35), 0, 0, 18, 18, 18, 18);
	}
}