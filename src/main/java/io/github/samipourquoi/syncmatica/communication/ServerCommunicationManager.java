package io.github.samipourquoi.syncmatica.communication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.Exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.Exchange.UploadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.VersionHandshakeServer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ServerCommunicationManager extends CommunicationManager {

	public ServerCommunicationManager(IFileStorage data, SyncmaticManager schematicManager) {
		super(data, schematicManager);
	}
	
	public void onPlayerJoin(ExchangeTarget newPlayer) {
		VersionHandshakeServer hi = new VersionHandshakeServer(newPlayer, this);
		startExchangeUnchecked(hi);
	}
	
	public void onPlayerLeave(ExchangeTarget oldPlayer) {
		Collection<Exchange> potentialMessageTarget = openExchange.get(oldPlayer);
		if (potentialMessageTarget != null) {
			for (Exchange target: potentialMessageTarget) {
				target.close();
			}
		}
		openExchange.remove(oldPlayer);
		broadcastTargets.remove(oldPlayer);
	}

	@Override
	protected void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REQUEST_LITEMATIC.IDENTIFIER)) {
			UUID syncmaticaId = packetBuf.readUuid();
			ServerPlacement placement = schematicManager.getPlacement(syncmaticaId);
			if (placement == null) {
				return;
			}
			File toUpload = fileStorage.getLocalLitematic(placement);
			UploadExchange upload = null;
			try {
				 upload = new UploadExchange(placement, toUpload, source, this);
			} catch (FileNotFoundException e) {
				// should be fine
				e.printStackTrace();
			}
			if (upload == null) {
				return;
			}
			this.startExchange(upload);
			return;
		}
		if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			ServerPlacement placement = receiveMetaData(packetBuf);
			if (schematicManager.getPlacement(placement.getId()) == null) {
				try {
					download(placement, source);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void handleExchange(Exchange exchange) {
		if (exchange instanceof VersionHandshakeServer && exchange.isSuccessful()) {
			broadcastTargets.add(exchange.getPartner());
			for (ServerPlacement placement: schematicManager.getAll()) {
				sendMetaData(placement, exchange.getPartner());
			}
		}
		if (exchange instanceof DownloadExchange && exchange.isSuccessful()) {
			ServerPlacement placement = ((DownloadExchange)exchange).getPlacement();
			schematicManager.addPlacement(placement);
			for (ExchangeTarget target: broadcastTargets) {
				sendMetaData(placement, target);
			}
		}
	}
}
