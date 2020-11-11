package io.github.samipourquoi.syncmatica.communication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.LocalLitematicState;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.Exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.Exchange.UploadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.VersionHandshakeServer;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ServerCommunicationManager extends CommunicationManager {
	
	private final Map<UUID, List<ServerPlacement>> downloadingFile = new HashMap<>();

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
				target.close(false);
				handleExchange(target);
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
				if (!Syncmatica.getFileStorage().getLocalState(placement).isLocalFileReady()) {
					// special edge case because files are transmitted by placement rather than file names/hashes
					if (Syncmatica.getFileStorage().getLocalState(placement) == LocalLitematicState.DOWNLOADING_LITEMATIC) {
						downloadingFile.computeIfAbsent(placement.getHash(), (key)->new ArrayList<>()).add(placement);
						return;
					}
					try {
						download(placement, source);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					addPlacement(source, placement);
				}
			} else {
				cancelShare(source, placement);
			}
			return;
		}
		if (id.equals(PacketType.REMOVE_SYNCMATIC.IDENTIFIER)) {
			UUID placementId = packetBuf.readUuid();
			ServerPlacement placement = schematicManager.getPlacement(placementId);
			if (placement != null) {
				schematicManager.removePlacement(placement);
				for (ExchangeTarget client: broadcastTargets) {
					PacketByteBuf newPacketBuf = new PacketByteBuf(Unpooled.buffer());
					newPacketBuf.writeUuid(placement.getId());
					client.sendPacket(PacketType.REMOVE_SYNCMATIC.IDENTIFIER, newPacketBuf);
				}
			}
		}
	}
	
	@Override
	protected void handleExchange(Exchange exchange) {
		if (exchange instanceof DownloadExchange) {
			ServerPlacement p = ((DownloadExchange)exchange).getPlacement();
			
			if (exchange.isSuccessful()) {
				addPlacement(exchange.getPartner(), p);
				if (downloadingFile.containsKey(p.getHash())) {
					for (ServerPlacement placement: downloadingFile.get(p.getHash())) {
						addPlacement(exchange.getPartner(), placement);
					}
				}
			} else {
				cancelShare(exchange.getPartner(), p);
				if (downloadingFile.containsKey(p.getHash())) {
					for (ServerPlacement placement: downloadingFile.get(p.getHash())) {
						cancelShare(exchange.getPartner(), placement);
					}
				}
			}
			
			downloadingFile.remove(p.getHash());
			return;
		}
		if (exchange instanceof VersionHandshakeServer && exchange.isSuccessful()) {
			broadcastTargets.add(exchange.getPartner());
		}
	}
	
	private void addPlacement(ExchangeTarget t, ServerPlacement placement) {
		if (schematicManager.getPlacement(placement.getId()) != null) {
			cancelShare(t, placement);
			return;
		}
		schematicManager.addPlacement(placement);
		for (ExchangeTarget target: broadcastTargets) {
			sendMetaData(placement, target);
		}
	}
	
	private void cancelShare(ExchangeTarget source, ServerPlacement placement) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(placement.getId());
		source.sendPacket(PacketType.CANCEL_SHARE.IDENTIFIER, packetByteBuf);
	}
}
