package io.github.samipourquoi.syncmatica.communication.Exchange;

import java.util.Collection;

import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class VersionHandshakeServer extends AbstractExchange {

	public VersionHandshakeServer(ExchangeTarget partner, CommunicationManager manager) {
		super(partner, manager);
	}

	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		return id.equals(PacketType.REGISTER_VERSION.IDENTIFIER);
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		if (Syncmatica.checkPartnerVersion(packetBuf.readString(32767))) {
			PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
			Collection<ServerPlacement> l = Syncmatica.getSyncmaticManager().getAll();
			newBuf.writeInt(l.size());
			for (ServerPlacement p: l) {
				getManager().putMetaData(p, newBuf);
			}
			getPartner().sendPacket(PacketType.CONFIRM_USER.IDENTIFIER, newBuf);
			succeed();
		} else {
			// same as client - avoid further packets
			close(false);
		}
		
	}

	@Override
	public void init() {
		PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
		newBuf.writeString(Syncmatica.VERSION);
		getPartner().sendPacket(PacketType.REGISTER_VERSION.IDENTIFIER, newBuf);
	}

	@Override
	protected void sendCancelPacket() {}

}
