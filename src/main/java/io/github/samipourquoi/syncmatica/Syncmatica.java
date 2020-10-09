package io.github.samipourquoi.syncmatica;

import java.io.File;

import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ServerCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import net.minecraft.client.MinecraftClient;

// could probably turn this into a singleton

public class Syncmatica {
	
	private static boolean isServer;
	private static boolean isInit = false;
	private static boolean isStarted = false;
	
	public static final String VERSION = "0.0.1";
	public static final String SERVER_PATH = "."+File.separator+"syncmatics";
	public static final String CLIENT_PATH = "."+File.separator+"schematics"+File.separator+".sync";
	
	private static CommunicationManager comms;
	private static FileStorage data;
	private static SchematicManager schematics;
	
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
		if (isInit) {
			return;
		}
		data = new FileStorage();
		schematics = new SchematicManager();
		if (isServer) {
			comms = new ServerCommunicationManager(data, schematics);
		} else {
			ExchangeTarget server = new ExchangeTarget(MinecraftClient.getInstance().getNetworkHandler());
			comms = new ClientCommunicationManager(data, schematics, server);
		}
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
		init();
		isStarted = true;
	}

	public static void shutdown() {
		deinit();
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
	
	public static boolean checkPartnerVersion(String partnerVersion) {
		// so far no unmatching or matching versions lol
		return true;
	}
	
}
