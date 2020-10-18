package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;

// pretty sure this is some kind of pattern 
// dont remember the name though
public class RedirectFileStorage implements IFileStorage {

	private final IFileStorage fs;
	
	
	// stores the redirects
	// will associate a hash with a specific file on the disk
	// not sure how to solve storage and make everything convenient for the user
	private final Map<UUID, RedirectData> redirect = new HashMap<>();
	
	public RedirectFileStorage() {
		fs = new FileStorage();
	}
	
	public void addRedirect(File file) {
		RedirectData red = new RedirectData(file);
		UUID hashId = UUID.nameUUIDFromBytes(red.getHash());
		redirect.put(hashId, red);
	}
	
	@Override
	public LocalLitematicState getLocalState(ServerPlacement placement) {
		UUID hashId = UUID.nameUUIDFromBytes(placement.getHash());
		if (redirect.containsKey(hashId) && Arrays.equals(redirect.get(hashId).getHash(), placement.getHash())) {
			return LocalLitematicState.LOCAL_LITEMATIC_PRESENT;
		} else {
			return fs.getLocalState(placement);
		}
	}

	@Override
	public File createLocalLitematic(ServerPlacement placement) {
		return fs.createLocalLitematic(placement);
	}

	@Override
	public File getLocalLitematic(ServerPlacement placement) {
		UUID hashId = UUID.nameUUIDFromBytes(placement.getHash());
		if (redirect.containsKey(hashId)) {
			RedirectData red = redirect.get(hashId);
			if (Arrays.equals(placement.getHash(), red.getHash())) {
				return red.redirect;
			} else {
				redirect.remove(hashId);
			}
		}
		return fs.getLocalLitematic(placement);
	}

	@Override
	public void setCommunitcationManager(CommunicationManager man) {
		fs.setCommunitcationManager(man);
	}
	
	private class RedirectData {
		File redirect = null;
		byte[] hash = null;
		long hashTimeStamp;
		
		RedirectData(File file) {
			redirect = file;
			getHash();
			if (hash == null) {
				file = null;
			}
		}
		
		byte[] getHash() {
			if (hashTimeStamp==redirect.lastModified()) {
				return hash;
			}
			try {
				hash = SyncmaticaUtil.createChecksum(new FileInputStream(redirect));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			hashTimeStamp = redirect.lastModified();
			return hash;
		}
	}

}
