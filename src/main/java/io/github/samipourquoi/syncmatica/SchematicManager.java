package io.github.samipourquoi.syncmatica;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SchematicManager {
	private final Map<UUID, ServerPlacement> schematics = new HashMap<>();
	
	public void addPlacement(ServerPlacement placement) {
		schematics.put(placement.getId(), placement);
	}
	
	public ServerPlacement getPlacement(UUID id) {
		return schematics.get(id);
	}
	
	public Collection<ServerPlacement> getAll() {
		return schematics.values();
	}
	
	public void removePlacement(ServerPlacement placement) {
		schematics.remove(placement.getId());
	}
	
}
