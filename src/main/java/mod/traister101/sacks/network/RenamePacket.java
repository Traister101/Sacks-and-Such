package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.objects.container.AbstractContainerRenameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.Charset;

public class RenamePacket implements IMessage {

    private String name;

    @Deprecated
    @SuppressWarnings("unused")
    public RenamePacket() {
    }

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
            EntityPlayer player = ctx.getServerHandler().player;
            if (player == null) return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx, player));
            return null;
        }

        private void handle(RenamePacket message, MessageContext ctx, EntityPlayer player) {
            if (player.openContainer instanceof AbstractContainerRenameable) {
                ((AbstractContainerRenameable) player.openContainer).updateItemName(message.name);
            }
        }
    }
}