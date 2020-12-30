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

import io.github.samipourquoi.syncmatica.LocalLitematicState;
import io.github.samipourquoi.syncmatica.communication.exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.exchange.UploadExchange;
import io.github.samipourquoi.syncmatica.communication.exchange.VersionHandshakeServer;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ServerCommunicationManager extends CommunicationManager {
	
	private final Map<UUID, List<ServerPlacement>> downloadingFile = new HashMap<>();

	public ServerCommunicationManager() {
		super();
	}
	
	public void onPlayerJoin(ExchangeTarget newPlayer) {
		VersionHandshakeServer hi = new VersionHandshakeServer(newPlayer, context);
		startExchangeUnchecked(hi);
	}
	
	public void onPlayerLeave(ExchangeTarget oldPlayer) {
		Collection<Exchange> potentialMessageTarget = oldPlayer.getExchanges();
		if (potentialMessageTarget != null) {
			for (Exchange target: potentialMessageTarget) {
				target.close(false);
				handleExchange(target);
			}
		}
		broadcastTargets.remove(oldPlayer);
	}

	@Override
	protected void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REQUEST_LITEMATIC.IDENTIFIER)) {
			UUID syncmaticaId = packetBuf.readUuid();
			ServerPlacement placement = context.getSyncmaticManager().getPlacement(syncmaticaId);
			if (placement == null) {
				return;
			}
			File toUpload = context.getFileStorage().getLocalLitematic(placement);
			UploadExchange upload = null;
			try {
				 upload = new UploadExchange(placement, toUpload, source, context);
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
			if (context.getSyncmaticManager().getPlacement(placement.getId()) == null) {
				if (!context.getFileStorage().getLocalState(placement).isLocalFileReady()) {
					// special edge case because files are transmitted by placement rather than file names/hashes
					if (context.getFileStorage().getLocalState(placement) == LocalLitematicState.DOWNLOADING_LITEMATIC) {
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
			ServerPlacement placement = context.getSyncmaticManager().getPlacement(placementId);
			if (placement != null) {
				context.getSyncmaticManager().removePlacement(placement);
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
		if (context.getSyncmaticManager().getPlacement(placement.getId()) != null) {
			cancelShare(t, placement);
			return;
		}
		context.getSyncmaticManager().addPlacement(placement);
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
