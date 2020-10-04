package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;

import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;

public class SyncmaticaLitematicaFileStorage {
	
	private static HashMap<SyncmaticaServerPlacement, Long> buffer = new HashMap<>();
	
	public static LocalLitematicState getLocalState(SyncmaticaServerPlacement placement) {
		File localFile = new File(Syncmatica.getSchematicPath(placement.getFileName()));
		if (localFile.isFile()) {
			if (isDownloading(placement)) {
				return LocalLitematicState.DOWNLOADING_LITEMATIC;
			}
			if ((buffer.containsKey(placement) && buffer.get(placement) == localFile.lastModified())||hashCompare(localFile, placement)) {
				return LocalLitematicState.LOCAL_LITEMATIC_PRESENT;
			}
			return LocalLitematicState.LOCAL_LITEMATIC_DESYNC;
		}
		return LocalLitematicState.NO_LOCAL_LITEMATIC;
	}
	
	private static boolean isDownloading(SyncmaticaServerPlacement placement) {
		// TODO Create functionality
		return false;
	}

	public static File getLocalLitematic(SyncmaticaServerPlacement placement) {
		if (getLocalState(placement).isLocalFileReady()) {
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
			buffer.put(placement, localFile.lastModified());
			return true;
		}
		return false;
	}
	
	public enum LocalLitematicState {
		NO_LOCAL_LITEMATIC(true, false),
		LOCAL_LITEMATIC_DESYNC(true, false),
		DOWNLOADING_LITEMATIC(false, false),
		LOCAL_LITEMATIC_PRESENT(false, true);
		
		private boolean downloadReady;
		private boolean fileReady;
		
		LocalLitematicState(boolean downloadReady, boolean fileReady) {
			this.downloadReady = downloadReady;
			this.fileReady = fileReady;
		}
		
		public boolean isReadyForDownload() {
			return downloadReady;
		}
		
		public boolean isLocalFileReady() {
			return fileReady;
		}
	}
}
