package io.github.samipourquoi.syncmatica.communication.exchange;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.FeatureSet;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class VersionHandshakeServer extends FeatureExchange {
	
	private String partnerVersion;
	
	public VersionHandshakeServer(ExchangeTarget partner, Context con) {
		super(partner, con);
	}

	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		return id.equals(PacketType.REGISTER_VERSION.IDENTIFIER)
				||super.checkPacket(id, packetBuf);
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REGISTER_VERSION.IDENTIFIER)) {
			partnerVersion = packetBuf.readString(32767);
			if (!getContext().checkPartnerVersion(partnerVersion)) {
				LogManager.getLogger(VersionHandshakeServer.class).info("Denying syncmatica join due to outdated client with local version {} and client version {}", Syncmatica.VERSION, partnerVersion);
				// same as client - avoid further packets
				close(false);
				return;
			}
			FeatureSet fs = FeatureSet.fromVersionString(partnerVersion);
			if (fs == null) {
				requestFeatureSet();
			} else {
				getPartner().setFeatureSet(fs);
				onFeatureSetReceive();
			}
		} else {
			super.handle(id, packetBuf);
		}
		
	}
	
	@Override
	public void onFeatureSetReceive() {
		LogManager.getLogger(VersionHandshakeServer.class).info("Syncmatica client joining with local version {} and client version {}", Syncmatica.VERSION, partnerVersion);
		PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
		Collection<ServerPlacement> l = getContext().getSyncmaticManager().getAll();
		newBuf.writeInt(l.size());
		for (ServerPlacement p: l) {
			getManager().putMetaData(p, newBuf);
		}
		getPartner().sendPacket(PacketType.CONFIRM_USER.IDENTIFIER, newBuf);
		succeed();
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
