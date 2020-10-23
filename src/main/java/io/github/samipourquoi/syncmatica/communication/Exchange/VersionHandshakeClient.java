package io.github.samipourquoi.syncmatica.communication.Exchange;

import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class VersionHandshakeClient extends AbstractExchange {

	public VersionHandshakeClient(ExchangeTarget partner, CommunicationManager manager) {
		super(partner, manager);
	}

	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		return id.equals(PacketType.CONFIRM_USER.IDENTIFIER)||id.equals(PacketType.REGISTER_VERSION.IDENTIFIER);
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REGISTER_VERSION.IDENTIFIER)) {
			String partnerVersion = packetBuf.readString(32767);
			if (!Syncmatica.checkPartnerVersion(partnerVersion)) {
				close();
			} else {
				PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
				newBuf.writeString(Syncmatica.VERSION);
				getPartner().sendPacket(PacketType.REGISTER_VERSION.IDENTIFIER, newBuf);
			}
		} else
		if (id.equals(PacketType.CONFIRM_USER.IDENTIFIER)) {
			Syncmatica.startup();
		}
	}

	@Override
	public void init() {}

}
