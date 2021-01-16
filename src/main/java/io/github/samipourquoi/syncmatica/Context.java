package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.util.Arrays;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.FeatureSet;

public class Context {
	
	private final IFileStorage files;
	private final CommunicationManager comMan;
	private final SyncmaticManager synMan;
	private FeatureSet fs = null;
	private boolean server;
	private final File litematicFolder;
	private boolean isStarted = false;
	
	public Context(IFileStorage fs, CommunicationManager comMan, SyncmaticManager synMan, boolean isServer, File litematicFolder) {
		this.files = fs;
		fs.setContext(this);
		this.comMan = comMan;
		comMan.setContext(this);
		this.synMan = synMan;
		synMan.setContext(this);
		server = isServer;
		this.litematicFolder = litematicFolder;
	}
	
	public IFileStorage getFileStorage() {return files;}
	public CommunicationManager getCommunicationManager() {return comMan;}
	public SyncmaticManager getSyncmaticManager() {return synMan;}
	
	public FeatureSet getFeatureSet() {
		if (fs == null) {
			generateFeatureSet();
		}
		return fs;
	}
	
	public boolean isServer() {return server;}
	public boolean isStarted() {return isStarted;}
	public File getLitematicFolder() {return litematicFolder;}

	private void generateFeatureSet() {
		fs = new FeatureSet(Arrays.asList(Feature.values()));
	}	
	public void startup() {
		isStarted = true;
		synMan.startup();
	}
	
	public void shutdown() {
		isStarted = false;
		synMan.shutdown();
	}
	
	public boolean checkPartnerVersion(String version) {
		return (!version.equals("0.0.1"));
	}

	public File getConfigFolder() {
		return new File(new File("."), "config"+File.separator+Syncmatica.MOD_ID);
	}
}
