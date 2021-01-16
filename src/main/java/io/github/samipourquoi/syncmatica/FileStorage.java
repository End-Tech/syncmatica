package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;

public class FileStorage implements IFileStorage {
	
	private HashMap<ServerPlacement, Long> buffer = new HashMap<>();
	private Context context = null;
	
	public FileStorage() {}
	
	public void setContext(Context con) {
		if (context == null) {
			context = con;
		} else {
			throw new RuntimeException("Duplicate Context assignment");
		}
	}
	
	public LocalLitematicState getLocalState(ServerPlacement placement) {
		File localFile = getSchematicPath(placement);
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
	
	private boolean isDownloading(ServerPlacement placement) {
		if (context == null) {
			throw new RuntimeException("No CommunicationManager has been set yet - cannot determ litematic state");
		}
		return context.getCommunicationManager().getDownloadState(placement);
	}

	public File getLocalLitematic(ServerPlacement placement) {
		if (getLocalState(placement).isLocalFileReady()) {
			return getSchematicPath(placement);
		} else {
			return null;
		}
	}
	
	// method for creating an empty file for the litematic data
	public File createLocalLitematic(ServerPlacement placement) {
		if (getLocalState(placement).isLocalFileReady()) {
			throw new IllegalArgumentException("");
		}
		File file = getSchematicPath(placement);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	private boolean hashCompare(File localFile, ServerPlacement placement) {
		UUID hash = null;
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
		if (hash.equals(placement.getHash())) {
			buffer.put(placement, localFile.lastModified());
			return true;
		}
		return false;
	}
	
	private File getSchematicPath(ServerPlacement placement) {
		File litematicPath = context.getLitematicFolder();
		if (context.isServer()) {
			return new File(litematicPath,placement.getHash().toString()+".litematic");
		}
		return new File(litematicPath,placement.getName().toString()+".litematic");
	}
}
