package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.objects.container.ContainerRenameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RenamePacket implements IMessage {

	private String name;

	@SuppressWarnings("unused")
	public RenamePacket() {
	}

	public RenamePacket(final String name) {
		this.name = name;
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
	}

	public static class Handler implements IMessageHandler<RenamePacket, IMessage> {

		@Override
		public IMessage onMessage(final RenamePacket message, final MessageContext ctx) {
			final EntityPlayer player = ctx.getServerHandler().player;
			if (player == null) return null;

			if (!(player.openContainer instanceof ContainerRenameable)) return null;

			FMLCommonHandler.instance()
					.getWorldThread(ctx.netHandler)
					.addScheduledTask(() -> ((ContainerRenameable) player.openContainer).updateItemName(message.name));
			return null;
		}
	}
}