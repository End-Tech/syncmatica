package io.github.samipourquoi.syncmatica;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SyncmaticManager {
	private final Map<UUID, ServerPlacement> schematics = new HashMap<>();
	private final Collection<Consumer<ServerPlacement>> consumers = new ArrayList<>();
	
	public void addPlacement(ServerPlacement placement) {
		schematics.put(placement.getId(), placement);
		updateServerPlacement(placement);
	}
	
	public ServerPlacement getPlacement(UUID id) {
		return schematics.get(id);
	}
	
	public Collection<ServerPlacement> getAll() {
		return schematics.values();
	}
	
	public void removePlacement(ServerPlacement placement) {
		schematics.remove(placement.getId());
		updateServerPlacement(placement);
	}
	
	public void addServerPlacementConsumer(Consumer<ServerPlacement> consumer) {
		consumers.add(consumer);
	}
	
	public void removeServerPlacementConsumer(Consumer<ServerPlacement> consumer) {
		consumers.remove(consumer);
	}
	
	public void updateServerPlacement(ServerPlacement updated) {
		for (Consumer<ServerPlacement> consumer: consumers) {
			consumer.accept(updated);
		}
	}
	
}
