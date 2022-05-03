package mod.traister101.sacks.util.handlers;

import static mod.traister101.sacks.SacksNSuch.MODID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.client.gui.GuiContainerSack;
import mod.traister101.sacks.objects.container.ContainerSack;
import mod.traister101.sacks.objects.items.ItemSack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final ResourceLocation SACK_SLOTS_1 = new ResourceLocation(MODID, "textures/gui/sack_1.png");
	public static final ResourceLocation SACK_SLOTS_4 = new ResourceLocation(MODID, "textures/gui/sack_4.png");

	public static void openGui(World world, EntityPlayer player, Type type) {
		player.openGui(SacksNSuch.getInstance(), type.ordinal(), world, 0, 0, 0);
	}

	@Override
	@Nullable
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		ItemStack stack = player.getHeldItemMainhand();

		Type type = Type.valueOf(ID);
		switch (type) {
		case THATCH:
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case LEATHER:
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case BURLAP:
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		case MINERS:
			return new ContainerSack(player.inventory, stack.getItem() instanceof ItemSack ? stack : player.getHeldItemOffhand());
		default:
			return null;
		}
	}

	@Override
	@Nullable
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Container container = getServerGuiElement(ID, player, world, x, y, z);
		Type type = Type.valueOf(ID);
		BlockPos pos = new BlockPos(x, y, z);

		switch (type) {

		case THATCH:
			return new GuiContainerSack(container, player.inventory, SACK_SLOTS_4);
		case LEATHER:
			return new GuiContainerSack(container, player.inventory, SACK_SLOTS_4);
		case BURLAP:
			return new GuiContainerSack(container, player.inventory, SACK_SLOTS_4);
		case MINERS:
			return new GuiContainerSack(container, player.inventory, SACK_SLOTS_1);
		default:
			return null;
		}
	}

	public enum Type {
		THATCH,
		LEATHER,
		BURLAP,
		MINERS,
		NULL;

		private static final Type[] values = values();

		@Nonnull
		public static Type valueOf(int id) {
			return id < 0 || id >= values.length ? NULL : values[id];
		}
	}
}
