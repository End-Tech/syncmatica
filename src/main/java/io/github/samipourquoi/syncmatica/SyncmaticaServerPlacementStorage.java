package io.github.samipourquoi.syncmatica;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class SyncmaticaServerPlacementStorage {
	private static HashMap<UUID, SyncmaticaServerPlacement> placements = new HashMap<>();
	
	public static void addPlacement(SyncmaticaServerPlacement placement) {
		placements.put(placement.getId(), placement);
	}
	
	public static SyncmaticaServerPlacement get(UUID id) {
		return placements.get(id);
	}
	
	public static Collection<SyncmaticaServerPlacement> getEntries() {
		return placements.values();
	}
	
	public static void removePlacement(SyncmaticaServerPlacement placement) {
		placements.remove(placement.getId());
	}

	public static boolean isLoaded(SyncmaticaServerPlacement entry) {
		// TODO Need to add functionality
		return false;
	}
}
