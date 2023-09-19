package mod.traister101.sacks.network;

import io.netty.buffer.ByteBuf;
import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.api.SackRegistry;
import mod.traister101.sacks.api.types.SackType;
import net.dries007.tfc.api.capability.size.Size;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SackTypeSync implements IMessage {

	private SackType sackType;
	private int slotCount;
	private int slotCapacity;
	private boolean doesAutoPickup;
	private boolean doesVoiding;
	private Size allowedSize;

	@SuppressWarnings("unused")
	public SackTypeSync() {
	}

	public SackTypeSync(final SackType sackType) {
		this.sackType = sackType;
		this.slotCount = sackType.getSlotCount();
		this.slotCapacity = sackType.getSlotCapacity();
		this.doesAutoPickup = sackType.doesAutoPickup();
		this.doesVoiding = sackType.doesVoiding();
		this.allowedSize = sackType.getAllowedSize();
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		sackType = ByteBufUtils.readRegistryEntry(buf, SackRegistry.SACK_TYPES);
		slotCount = buf.readInt();
		slotCapacity = buf.readInt();
		doesAutoPickup = buf.readBoolean();
		doesVoiding = buf.readBoolean();
		allowedSize = Size.values()[buf.readByte()];
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		ByteBufUtils.writeRegistryEntry(buf, sackType);
		buf.writeInt(slotCount);
		buf.writeInt(slotCapacity);
		buf.writeBoolean(doesAutoPickup);
		buf.writeBoolean(doesVoiding);
		buf.writeByte(allowedSize.ordinal());
	}

	public static class Handler implements IMessageHandler<SackTypeSync, IMessage> {

		@Override
		public IMessage onMessage(final SackTypeSync message, final MessageContext ctx) {
			final SackType sackType = message.sackType;

			sackType.setSlotCount(message.slotCount);
			sackType.setSlotCapacity(message.slotCapacity);
			sackType.setDoesAutoPickup(message.doesAutoPickup);
			sackType.setDoesVoiding(message.doesVoiding);
			sackType.setAllowedSize(message.allowedSize);

			SacksNSuch.getLog().info("Sycned sack type {} with the server", sackType);
			return null;
		}
	}
}