package io.github.samipourquoi.syncmatica;

import java.io.File;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;

// could probably turn this into a singleton

public class Syncmatica {
	
	private static boolean isServer;
	private static boolean isInit = false;
	private static boolean isStarted = false;
	
	public static final String VERSION = "0.0.1";
	public static final String SERVER_PATH = "."+File.separator+"syncmatics";
	public static final String CLIENT_PATH = "."+File.separator+"schematics"+File.separator+".sync";
	
	private static CommunicationManager comms;
	private static IFileStorage data;
	private static SyncmaticManager schematics;
	
	public static String getSchematicPath(String fileName) {
		return getStoragePath()+File.separator+fileName+".litematic";
	}
	
	private static String getStoragePath() {
		if (isServer) {
			return SERVER_PATH;
		} else {
			return CLIENT_PATH;
		}
	}
	
	public static void initServer(CommunicationManager comms, IFileStorage fileStorage, SyncmaticManager schematics) {
		File file = new File(SERVER_PATH);
		file.mkdirs();
		isServer = true;
		init(comms, fileStorage, schematics);
	}

	public static void initClient(CommunicationManager comms, IFileStorage fileStorage, SyncmaticManager schematics) {
		File file = new File(CLIENT_PATH);
		file.mkdirs();
		isServer = false;
		init(comms, fileStorage, schematics);
	}
	
	private static void init(CommunicationManager comms, IFileStorage fileStorage, SyncmaticManager schematics) {
		if (isInit) {
			return;
		}
		data = new FileStorage();
		Syncmatica.comms = comms;
		Syncmatica.schematics = schematics;
		data = fileStorage;
		//	ExchangeTarget server = new ExchangeTarget(MinecraftClient.getInstance().getNetworkHandler());
		//	comms = new ClientCommunicationManager(data, schematics, server);
		isInit = true;
	}
	
	private static void deinit() {
		if (!isInit) {
			return;
		}
		data = null;
		comms = null;
		isInit = false;
	}
	
	public static void startup() {
		if (!isInit) {
			throw new RuntimeException("Started Syncmatica before initializing it");
		}
		isStarted = true;
	}

	public static void shutdown() {
		deinit();
		isStarted = false;
	}
	
	public static CommunicationManager getCommunicationManager() {
		return comms;
	}
	
	public static IFileStorage getFileStorage() {
		return data;
	}
	
	public static SyncmaticManager getSyncmaticManager() {
		return schematics;
	}
	
	public static boolean isServer() {
		return isServer;
	}
	
	public static boolean isStarted() {
		return isStarted;
	}
	
	public static boolean checkPartnerVersion(String partnerVersion) {
		// so far no unmatching or matching versions lol
		return true;
	}
	
}
