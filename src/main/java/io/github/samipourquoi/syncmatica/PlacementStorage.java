package io.github.samipourquoi.syncmatica;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlacementStorage {
	private static HashMap<UUID, ServerPlacement> placements = new HashMap<>();
	
	public static void addPlacement(ServerPlacement placement) {
		placements.put(placement.getId(), placement);
	}
	
	public static ServerPlacement get(UUID id) {
		return placements.get(id);
	}
	
	public static Collection<ServerPlacement> getEntries() {
		return placements.values();
	}
	
	public static void removePlacement(ServerPlacement placement) {
		placements.remove(placement.getId());
	}

	public static boolean isLoaded(ServerPlacement entry) {
		// TODO: Need to add functionality
		return false;
	}
}
