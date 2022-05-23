package mod.traister101.sacks.network;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.objects.container.AbstractContainerRenameable;
import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RenamePacket implements IMessage {
	
	private String name;
	
	@Deprecated
	@SuppressWarnings("unused")
	public RenamePacket() {}
	
	public RenamePacket(String name) {
		this.name = name;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeCharSequence(name, Charset.defaultCharset());
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		name = buf.toString(Charset.defaultCharset());
	}
	
	public static class Handler implements IMessageHandler<RenamePacket, IMessage> {
		
		@Override
		public IMessage onMessage(RenamePacket message, MessageContext ctx) {
			// Why not tfc is already a dependency
			EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
			if (player != null) {
				TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
					if (player.openContainer instanceof AbstractContainerRenameable) {
						((AbstractContainerRenameable) player.openContainer).updateItemName(message.name);
					}
				});
			}
			return null;
		}
	}
}