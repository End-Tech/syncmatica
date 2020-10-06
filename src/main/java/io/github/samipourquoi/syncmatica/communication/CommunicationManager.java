package io.github.samipourquoi.syncmatica.communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.samipourquoi.syncmatica.SyncmaticaLitematicaFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacement;
import io.github.samipourquoi.syncmatica.communication.Exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CommunicationManager {
	private final Collection<ExchangeTarget> broadcastTargets = new ArrayList<>();
	private final Map<ExchangeTarget, Collection<Exchange>> openExchange = new HashMap<>();
	
	public void onPacket(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		// one of the syncmatica packet types
		Exchange handler = null;
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
		if (!SyncmaticaLitematicaFileStorage.getLocalState(syncmatic).isLocalFileReady()) {
			throw new IllegalArgumentException(syncmatic.toString()+" is not locally available");
		}
		Exchange downloadExchange = new DownloadExchange(syncmatic, source, this);
		startExchange(downloadExchange);
	}

	private void startExchange(Exchange newExchange) {
		if (!broadcastTargets.contains(newExchange.getPartner())) {
			throw new IllegalArgumentException(newExchange.getPartner().toString()+" is not a valid ExchangeTarget");
		}
		openExchange.computeIfAbsent(newExchange.getPartner(), (k) -> new ArrayList<>()).add(newExchange);
		newExchange.init();
	}
	
	
	
}
