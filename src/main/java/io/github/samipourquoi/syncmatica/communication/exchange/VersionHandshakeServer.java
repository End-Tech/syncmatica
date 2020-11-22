package io.github.samipourquoi.syncmatica.communication.exchange;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;

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
		String partnerVersion = packetBuf.readString(32767);
		if (Syncmatica.checkPartnerVersion(partnerVersion)) {
			LogManager.getLogger(VersionHandshakeServer.class).info("Syncmatica client joining with local version {} and client version {}", Syncmatica.VERSION, partnerVersion);
			PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
			Collection<ServerPlacement> l = Syncmatica.getSyncmaticManager().getAll();
			newBuf.writeInt(l.size());
			for (ServerPlacement p: l) {
				getManager().putMetaData(p, newBuf);
			}
			getPartner().sendPacket(PacketType.CONFIRM_USER.IDENTIFIER, newBuf);
			succeed();
		} else {
			LogManager.getLogger(VersionHandshakeServer.class).info("Denying syncmatica join due to outdated client with local version {} and client version {}", Syncmatica.VERSION, partnerVersion);
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
