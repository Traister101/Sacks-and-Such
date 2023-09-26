package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.ConfigSNS;
import mod.traister101.sacks.util.handlers.PickBlockHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class PickBlockPacket implements IMessage {

	private ItemStack stackToSelect;

	@SuppressWarnings("unused")
	public PickBlockPacket() {
	}

	@SideOnly(Side.CLIENT)
	public PickBlockPacket(final ItemStack stackToSelect) {
		this.stackToSelect = stackToSelect;
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		stackToSelect = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, stackToSelect);
	}

	public static class Handler implements IMessageHandler<PickBlockPacket, IMessage> {

		@Override
		@Nullable
		public IMessage onMessage(final PickBlockPacket message, final MessageContext ctx) {
			// Check if we should handle pick block (Server)
			if (!ConfigSNS.doPickBlock) return null;

			final EntityPlayerMP player = ctx.getServerHandler().player;
			if (player == null) return null;
			FMLCommonHandler.instance()
					.getWorldThread(ctx.netHandler)
					.addScheduledTask(() -> PickBlockHandler.handlePickBlock(player, message.stackToSelect));
			return null;
		}
	}
}