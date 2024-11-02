package mod.traister101.sns.network;

import mod.traister101.sns.SacksNSuch;
import org.apache.commons.lang3.mutable.MutableInt;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.*;

public final class SNSPacketHandler {

	private static final String VERSION = ModList.get().getModFileById(SacksNSuch.MODID).versionString();
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(SacksNSuch.MODID, "main"), () -> VERSION,
			VERSION::equals, VERSION::equals);
	private static final MutableInt ID = new MutableInt(0);

	public static void send(final PacketDistributor.PacketTarget target, final Object message) {
		CHANNEL.send(target, message);
	}

	/**
	 * Shorthand for {@code SNSPacketHandler.send(PacketDistributor.SERVER.noArg(), message);}
	 */
	public static void sendToServer(final Object message) {
		send(PacketDistributor.SERVER.noArg(), message);
	}

	public static void init() {
		// Client -> Server
		register(ServerboundPickBlockPacket.class, ServerboundPickBlockPacket::encode, ServerboundPickBlockPacket::new,
				ServerboundPickBlockPacket::handle);
		register(ServerboundTogglePacket.class, ServerboundTogglePacket::encode, ServerboundTogglePacket::new, ServerboundTogglePacket::handle);
		register(ServerboundPacketCycleSlotPacket.class, ServerboundPacketCycleSlotPacket::encode, ServerboundPacketCycleSlotPacket::new,
				ServerboundPacketCycleSlotPacket::handle);
		register(ServerboundToggleSlotVoidingPacket.class, ServerboundToggleSlotVoidingPacket::encode, ServerboundToggleSlotVoidingPacket::new,
				ServerboundToggleSlotVoidingPacket::handle);
		register(ServerboundOpenContainerPacket.class, ServerboundOpenContainerPacket::encode, ServerboundOpenContainerPacket::new,
				ServerboundOpenContainerPacket::handle);

		register(ClientboundBreakHorseshoePacket.class, ClientboundBreakHorseshoePacket::encode, ClientboundBreakHorseshoePacket::new,
				ClientboundBreakHorseshoePacket::handle);
	}

	private static <T> void register(@SuppressWarnings("SameParameterValue") final Class<T> clazz, final BiConsumer<T, FriendlyByteBuf> encoder,
			final Function<FriendlyByteBuf, T> decoder, final Consumer<T> handler) {
		register(clazz, encoder, decoder, (packet, player) -> handler.accept(packet));
	}

	private static <T> void register(final Class<T> clazz, final BiConsumer<T, FriendlyByteBuf> encoder, final Function<FriendlyByteBuf, T> decoder,
			final BiConsumer<T, ServerPlayer> handler) {
		CHANNEL.registerMessage(ID.getAndIncrement(), clazz, encoder, decoder, (packet, context) -> {
			context.get().setPacketHandled(true);
			context.get().enqueueWork(() -> handler.accept(packet, context.get().getSender()));
		});
	}
}