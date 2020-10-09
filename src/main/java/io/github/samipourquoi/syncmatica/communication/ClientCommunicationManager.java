package io.github.samipourquoi.syncmatica.communication;

import io.github.samipourquoi.syncmatica.FileStorage;
import io.github.samipourquoi.syncmatica.SchematicManager;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.Exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.Exchange.VersionHandshakeClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientCommunicationManager extends CommunicationManager {

	public ClientCommunicationManager(FileStorage storage, SchematicManager schematicManager, ExchangeTarget server) {
		super(storage, schematicManager);
		broadcastTargets.add(server);
		VersionHandshakeClient hi = new VersionHandshakeClient(server, this);
		startExchange(hi);
	}

	@Override
	protected void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			ServerPlacement placement = receiveMetaData(packetBuf);
			schematicManager.addPlacement(placement);
		}
		
	}

	@Override
	protected void handleExchange(Exchange exchange) {
	}

}
