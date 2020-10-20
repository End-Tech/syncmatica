package io.github.samipourquoi.syncmatica.communication;

import org.apache.logging.log4j.LogManager;

import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.Exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.Exchange.VersionHandshakeClient;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientCommunicationManager extends CommunicationManager {
	
	private final ExchangeTarget server;
	
	public ClientCommunicationManager(IFileStorage storage, SyncmaticManager schematicManager, ExchangeTarget server) {
		super(storage, schematicManager);
		this.server = server;
		broadcastTargets.add(server);
		VersionHandshakeClient hi = new VersionHandshakeClient(server, this);
		startExchangeUnchecked(hi);
	}
	
	public ExchangeTarget getServer() {
		return server;
	}

	@Override
	protected void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		LogManager.getLogger(ClientPlayNetworkHandler.class).info("received message with no handler");
		if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			ServerPlacement placement = receiveMetaData(packetBuf);
			schematicManager.addPlacement(placement);
		}
	}

	@Override
	protected void handleExchange(Exchange exchange) {
		if (exchange instanceof DownloadExchange && exchange.isSuccessful()) {
			LitematicManager.getInstance().renderSyncmatic(((DownloadExchange)exchange).getPlacement());
		}
	}

}
