package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class RenameHeldItemPacket implements IMessage {

	private boolean inMainhand;
	private String name;

	@SuppressWarnings("unused")
	public RenameHeldItemPacket() {
	}

	@SideOnly(Side.CLIENT)
	public RenameHeldItemPacket(final String name, final boolean inMainHand) {
		this.name = name;
		this.inMainhand = inMainHand;
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		inMainhand = buf.readBoolean();
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeBoolean(inMainhand);
	}

	public static class Handler implements IMessageHandler<RenameHeldItemPacket, IMessage> {

		@Override
		@Nullable
		public IMessage onMessage(final RenameHeldItemPacket message, final MessageContext ctx) {
			final EntityPlayer player = ctx.getServerHandler().player;
			if (player == null) return null;

			FMLCommonHandler.instance()
					.getWorldThread(ctx.netHandler)
					.addScheduledTask(() -> {
						final ItemStack itemStack = player.getHeldItem(message.inMainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

						if (StringUtils.isBlank(message.name)) {
							itemStack.clearCustomName();
						} else {
							// Sets new name with no italics
							itemStack.setStackDisplayName(TextFormatting.RESET + message.name);
						}

					});
			return null;
		}
	}
}