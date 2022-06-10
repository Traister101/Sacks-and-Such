package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.objects.items.ItemSack;
import mod.traister101.sacks.objects.items.ItemThrowableVessel;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TogglePacket implements IMessage {
	
	private ToggleType type;
	private boolean toggle;
	
	@Deprecated
	@SuppressWarnings("unused")
	public TogglePacket() {}
	
	public TogglePacket(boolean toggle, ToggleType type) {
		this.toggle = toggle;
		this.type = type;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(toggle);
		buf.writeInt(type.ordinal());
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		toggle = buf.readBoolean();
		type = ToggleType.valueOf(buf.readInt());
	}
	
	public static class Handler implements IMessageHandler<TogglePacket, IMessage> {
		
		@Override
		public IMessage onMessage(TogglePacket message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;
			if (player == null) return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
			return null;
		}
		
		private void handle(TogglePacket message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;
			ItemStack stack = player.getHeldItemMainhand();
			// Only throwable vessels can be sealed
			if (message.type == ToggleType.SEAL) {
				if (!(stack.getItem() instanceof ItemThrowableVessel)) {
					stack = player.getHeldItemOffhand();
					if (!(stack.getItem() instanceof ItemThrowableVessel)) return;
				}
			} else {
				if (!(stack.getItem() instanceof ItemSack)) {
					stack = player.getHeldItemOffhand();
					if (!(stack.getItem() instanceof ItemSack)) return;
				}
			}
			
			SNSUtils.toggle(stack, message.type, message.toggle);
		}
	}
}