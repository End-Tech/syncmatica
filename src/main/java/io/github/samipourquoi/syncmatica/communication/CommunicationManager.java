package io.github.samipourquoi.syncmatica.communication;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.samipourquoi.syncmatica.FileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacement;
import io.github.samipourquoi.syncmatica.communication.Exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CommunicationManager {
	private final Collection<ExchangeTarget> broadcastTargets = new ArrayList<>();
	private final Map<ExchangeTarget, Collection<Exchange>> openExchange = new HashMap<>();
	private final Map<SyncmaticaServerPlacement,Boolean> downloadState = new HashMap<>();
	private final FileStorage fileStorage;
	
	public CommunicationManager(FileStorage storage) {
		fileStorage = storage;
		storage.setCommunitcationManager(this);
	}
	
	public void onPacket(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		// TODO: Timeout
		Exchange handler = null;
		// id is one of the syncmatica packet types
		if (!PacketType.containsIdentifier(id)) {
			return;
		}
		Collection<Exchange> potentialMessageTarget = openExchange.get(source);
		if (potentialMessageTarget != null) {
			for (Exchange target: potentialMessageTarget) {
				if (target.checkPackage(id, packetBuf)) {
					target.handle(id, packetBuf);
					handler = target;
					break;
				}
			}
		}
		if (handler == null) {
			handle(source, id, packetBuf);
		} else if (handler.isFinished()){
			 potentialMessageTarget.remove(handler);
		}
	}
	
	private void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		// default implementation do not handle or respond to any packages
		// subclasses can override handle to implement server or client specific behavior
	}

	public void download(SyncmaticaServerPlacement syncmatic, ExchangeTarget source) throws NoSuchAlgorithmException, IOException {
		if (fileStorage.getLocalState(syncmatic).isReadyForDownload()) {
			throw new IllegalArgumentException(syncmatic.toString()+" is not ready for download");
		}
		File toDownload = fileStorage.createLocalLitematic(syncmatic);
		Exchange downloadExchange = new DownloadExchange(syncmatic, toDownload, source, this);
		setDownloadState(syncmatic, true);
		startExchange(downloadExchange);
	}

	public void setDownloadState(SyncmaticaServerPlacement syncmatic, boolean b) {
		downloadState.put(syncmatic, b);
	}
	
	public boolean getDownloadState(SyncmaticaServerPlacement syncmatic) {
		return downloadState.getOrDefault(syncmatic, false);
	}

	private void startExchange(Exchange newExchange) {
		if (!broadcastTargets.contains(newExchange.getPartner())) {
			throw new IllegalArgumentException(newExchange.getPartner().toString()+" is not a valid ExchangeTarget");
		}
		openExchange.computeIfAbsent(newExchange.getPartner(), (k) -> new ArrayList<>()).add(newExchange);
		newExchange.init();
	}
	
}
