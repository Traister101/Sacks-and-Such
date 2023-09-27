package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.api.registries.SackRegistry;
import mod.traister101.sacks.api.types.SackType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class SackTypeSync implements IMessage {

	private NBTTagCompound nbtTagCompound;
	private SackType sackType;

	@SuppressWarnings("unused")
	public SackTypeSync() {
	}

	public SackTypeSync(final SackType sackType) {
		this.sackType = sackType;
		this.nbtTagCompound = sackType.serializeNBT();
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		sackType = ByteBufUtils.readRegistryEntry(buf, SackRegistry.SACK_TYPES);
		nbtTagCompound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		ByteBufUtils.writeRegistryEntry(buf, sackType);
		ByteBufUtils.writeTag(buf, nbtTagCompound);
	}

	public static class Handler implements IMessageHandler<SackTypeSync, IMessage> {

		@Override
		@Nullable
		public IMessage onMessage(final SackTypeSync message, final MessageContext ctx) {
			message.sackType.deserializeNBT(message.nbtTagCompound);

			SacksNSuch.getLog().info("Sycned {} with the server", message.sackType);
			return null;
		}
	}
}