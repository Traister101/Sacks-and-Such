package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.util.handlers.PickBlockHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PickBlockPacket implements IMessage {

    public PickBlockPacket() {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PickBlockPacket, IMessage> {

        @Override
        public IMessage onMessage(PickBlockPacket message, MessageContext ctx) {
            final EntityPlayer player = ctx.getServerHandler().player;
            if (player == null) return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> PickBlockHandler.handlePickBlock(player));
            return null;
        }
    }
}