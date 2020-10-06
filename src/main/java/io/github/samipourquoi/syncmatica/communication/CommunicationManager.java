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

public class CommunicationManager {
	private final Collection<ExchangeTarget> broadcastTargets = new ArrayList<>();
	private final Map<ExchangeTarget, Collection<Exchange>> openExchange = new HashMap<>();
	
	public void addTarget(ExchangeTarget target) {
		broadcastTargets.add(target);
	}
	
	public void download(SyncmaticaServerPlacement syncmatic, ExchangeTarget source) throws NoSuchAlgorithmException, IOException {
		if (!SyncmaticaLitematicaFileStorage.getLocalState(syncmatic).isLocalFileReady()) {
			throw new IllegalArgumentException(syncmatic.toString()+" is not locally available");
		}
		Exchange downloadExchange = new DownloadExchange(syncmatic, source, this);
		startExchange(downloadExchange);
	}

	private void startExchange(Exchange newExchange) {
		// if (!broadcastTargets.contains(source)); {
		// throw new IllegalArgumentException(source.toString()+" is not a valid ExchangeTarget");
		//}
		openExchange.computeIfAbsent(newExchange.getPartner(), (k) -> new ArrayList<>()).add(newExchange);
		newExchange.init();
	}
	
	
	
}
