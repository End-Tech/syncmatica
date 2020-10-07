package io.github.samipourquoi.syncmatica;

import java.io.File;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;

// could probably turn this into a singleton

public class Syncmatica {
	
	private static boolean isServer;
	private static boolean isStarted = false;
	
	public static final String SERVER_PATH = "."+File.separator+"syncmatics";
	public static final String CLIENT_PATH = "."+File.separator+"schematics"+File.separator+".sync";
	
	private static CommunicationManager comms;
	private static FileStorage data;
	
	public static String getSyncmaticaPlacementPath(String fileName) {
		return getStoragePath()+File.separator+fileName+".litematic";
	}
	
	public static String getSchematicPath(String fileName) {
		return getStoragePath()+File.separator+fileName+".sync";
	}
	
	private static String getStoragePath() {
		if (isServer) {
			return SERVER_PATH;
		} else {
			return CLIENT_PATH;
		}
	}
	
	public static void initServer() {
		File file = new File(SERVER_PATH);
		file.mkdirs();
		isServer = true;
		init();
	}

	public static void initClient() {
		File file = new File(CLIENT_PATH);
		file.mkdirs();
		isServer = false;
		init();
	}
	
	private static void init() {
		data = new FileStorage();
		comms = new CommunicationManager(data);
		isStarted = true;
	}
	
	public static void shutdown() {
		data = null;
		comms = null;
		isStarted = false;
	}
	
	public static CommunicationManager getCommunicationManager() {
		return comms;
	}
	
	public static FileStorage getFileStorage() {
		return data;
	}
	
	public static boolean isServer() {
		return isServer;
	}
	
	public static boolean isStarted() {
		return isStarted;
	}
}
