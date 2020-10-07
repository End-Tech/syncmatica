package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;

public class FileStorage {
	
	private HashMap<SyncmaticaServerPlacement, Long> buffer = new HashMap<>();
	private CommunicationManager manager = null;
	
	public FileStorage() {}
	
	public void setCommunitcationManager(CommunicationManager man) {
		if (manager == null) {
			manager = man;
		} else {
			throw new RuntimeException("Duplicate CommunicationManager assignment");
		}
	}
	
	public LocalLitematicState getLocalState(SyncmaticaServerPlacement placement) {
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
	
	private boolean isDownloading(SyncmaticaServerPlacement placement) {
		if (manager == null) {
			throw new RuntimeException("No CommunicationManager has been set yet - cannot determ litematic state");
		}
		return manager.getDownloadState(placement);
	}

	public File getLocalLitematic(SyncmaticaServerPlacement placement) {
		if (getLocalState(placement).isLocalFileReady()) {
			return new File(Syncmatica.getSchematicPath(placement.getFileName()));
		} else {
			return null;
		}
	}
	
	// method for creating an empty file for the litematic data
	public File createLocalLitematic(SyncmaticaServerPlacement placement) throws IOException {
		if (getLocalState(placement).isLocalFileReady()) {
			throw new IllegalArgumentException("");
		}
		File file = new File(Syncmatica.getSchematicPath(placement.getFileName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		return file;
	}

	private boolean hashCompare(File localFile, SyncmaticaServerPlacement placement) {
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
