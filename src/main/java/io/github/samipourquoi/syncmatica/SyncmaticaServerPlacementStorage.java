package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;

public class SyncmaticaServerPlacementStorage {
	private static HashMap<UUID, SyncmaticaServerPlacement> placements = new HashMap<>();
	private static HashMap<SyncmaticaServerPlacement, Boolean> buffer = new HashMap<>();
	
	public static void addPlacement(SyncmaticaServerPlacement placement) {
		placements.put(placement.getId(), placement);
	}
	
	public static SyncmaticaServerPlacement get(UUID id) {
		return placements.get(id);
	}
	
	public static Collection<SyncmaticaServerPlacement> getEntries() {
		return placements.values();
	}
	
	public static boolean hasLocalLitematic(SyncmaticaServerPlacement placement) {
		File localFile = new File(Syncmatica.getSchematicPath(placement.getFileName()));
		if (localFile.isFile()) {
			if (buffer.containsKey(placement)||hashCompare(localFile, placement)) {
				return true;
			}
		}
		return false;
	}
	
	public static File getLocalLitematic(SyncmaticaServerPlacement placement) {
		if (hasLocalLitematic(placement)) {
			return new File(Syncmatica.getSchematicPath(placement.getFileName()));
		} else {
			return null;
		}
	}

	private static boolean hashCompare(File localFile, SyncmaticaServerPlacement placement) {
		byte[] hash = null;
		try {
			hash = SyncmaticaUtil.createChecksum(new FileInputStream(localFile));
		} catch (FileNotFoundException e) {
			// can be safely ignored since we established that file has been found
			e.printStackTrace();
		} catch (Exception e) {
			// wtf just exception?
			e.printStackTrace();
		}
		if (hash == null) {
			return false;
		}
		if (Arrays.equals(hash, placement.getHash())) {
			buffer.put(placement, true);
			return true;
		}
		return false;
	}
}
