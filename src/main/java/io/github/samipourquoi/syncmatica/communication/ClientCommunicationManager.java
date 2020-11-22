package io.github.samipourquoi.syncmatica.communication;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;


import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.communication.exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.exchange.VersionHandshakeClient;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientCommunicationManager extends CommunicationManager {
	
	private final ExchangeTarget server;
	private final Collection<ServerPlacement> sharing;
	
	public ClientCommunicationManager(IFileStorage storage, SyncmaticManager schematicManager, ExchangeTarget server) {
		super(storage, schematicManager);
		this.server = server;
		broadcastTargets.add(server);
		sharing = new HashSet<>();
		VersionHandshakeClient hi = new VersionHandshakeClient(server, this);
		startExchangeUnchecked(hi);
	}
	
	public ExchangeTarget getServer() {
		return server;
	}

	@Override
	protected void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			ServerPlacement placement = receiveMetaData(packetBuf);
			schematicManager.addPlacement(placement);
		}
		if (id.equals(PacketType.REMOVE_SYNCMATIC.IDENTIFIER)) {
			UUID placementId = packetBuf.readUuid();
			ServerPlacement placement = schematicManager.getPlacement(placementId);
			if (placement != null) {
				schematicManager.removePlacement(placement);
				if (LitematicManager.getInstance().isRendered(placement)) {
					LitematicManager.getInstance().unrenderSyncmatic(placement);
				}
			}
		}
	}

	@Override
	protected void handleExchange(Exchange exchange) {
		if (exchange instanceof DownloadExchange && exchange.isSuccessful()) {
			LitematicManager.getInstance().renderSyncmatic(((DownloadExchange)exchange).getPlacement());
		}
	}
	
	@Override
	public void setDownloadState(ServerPlacement syncmatic, boolean state) {
		downloadState.put(syncmatic.getHash(), state);
		if (state) { //change client behavior so that the Load button doesn't show up naturally
			schematicManager.updateServerPlacement(syncmatic);
		}
	}
	
	public void setSharingState(ServerPlacement placement, boolean state) {
		if (state) {
			if (!sharing.contains(placement)) {
				sharing.add(placement);
			}
		} else if (sharing.contains(placement)) {
			sharing.remove(placement);
		}
	}
	
	public boolean getSharingState(ServerPlacement placement) {
		return sharing.contains(placement);
	}

}
