package mod.traister101.sacks.util.handlers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.gui.GuiContainerSack;
import mod.traister101.sacks.client.gui.GuiContainerThrowableVessel;
import mod.traister101.sacks.objects.container.ContainerSack;
import mod.traister101.sacks.objects.container.ContainerThrowableVessel;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mod.traister101.sacks.SacksNSuch.MODID;

public final class GuiHandler implements IGuiHandler {

	public static final ResourceLocation BACKGROUND = new ResourceLocation(MODID, "textures/gui/plain.png");

	public static void openGui(final World world, final EntityPlayer player, final GuiType guiType) {
		player.openGui(SacksNSuch.getInstance(), guiType.ordinal(), world, 0, 0, 0);
	}

	@Override
	@Nullable
	public Container getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		final ItemStack heldStack = player.getHeldItemMainhand();
		switch (GuiType.valueOf(ID)) {
			case SACK:
				return new ContainerSack(player.inventory, heldStack.getItem() instanceof ItemSack ? heldStack : player.getHeldItemOffhand());
			case VESSEL_EXPLOSIVE:
				return new ContainerThrowableVessel(player.inventory,
						heldStack.getItem() instanceof ItemThrowableVessel ? heldStack : player.getHeldItemOffhand());
			default:
				return null;
		}
	}

	@Override
	@Nullable
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		final Container container = getServerGuiElement(ID, player, world, x, y, z);
		final ItemStack heldStack = player.getHeldItemMainhand();
		switch (GuiType.valueOf(ID)) {
			case SACK:
				return new GuiContainerSack(container, player.inventory, BACKGROUND,
						heldStack.getItem() instanceof ItemSack ? heldStack : player.getHeldItemOffhand());
			case VESSEL_EXPLOSIVE:
				return new GuiContainerThrowableVessel(container, BACKGROUND,
						heldStack.getItem() instanceof ItemThrowableVessel ? heldStack : player.getHeldItemOffhand());
			default:
				return null;
		}
	}

	public enum GuiType {
		SACK,
		VESSEL_EXPLOSIVE,
		NULL;

		@Nonnull
		public static GuiType valueOf(int id) {
			return id < 0 || id >= values().length ? NULL : values()[id];
		}
	}
}
