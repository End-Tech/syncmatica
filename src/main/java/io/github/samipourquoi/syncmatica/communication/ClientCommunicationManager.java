package io.github.samipourquoi.syncmatica.communication;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;


import io.github.samipourquoi.syncmatica.communication.exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.exchange.VersionHandshakeClient;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientCommunicationManager extends CommunicationManager {
	
	private final ExchangeTarget server;
	private final Collection<ServerPlacement> sharing;
	
	public ClientCommunicationManager(ExchangeTarget server) {
		super();
		this.server = server;
		broadcastTargets.add(server);
		sharing = new HashSet<>();
	}
	
	public ExchangeTarget getServer() {
		return server;
	}

	@Override
	protected void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			ServerPlacement placement = receiveMetaData(packetBuf);
			context.getSyncmaticManager().addPlacement(placement);
		}
		if (id.equals(PacketType.REMOVE_SYNCMATIC.IDENTIFIER)) {
			UUID placementId = packetBuf.readUuid();
			ServerPlacement placement = context.getSyncmaticManager().getPlacement(placementId);
			if (placement != null) {
				context.getSyncmaticManager().removePlacement(placement);
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
			context.getSyncmaticManager().updateServerPlacement(syncmatic);
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
	
	@Override
	public void setContext(Context con) {
		super.setContext(con);
		VersionHandshakeClient hi = new VersionHandshakeClient(server, context);
		startExchangeUnchecked(hi);
	}

}
