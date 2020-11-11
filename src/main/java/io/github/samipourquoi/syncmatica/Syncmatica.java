package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;

// could probably turn this into a singleton

public class Syncmatica {
	
	private static boolean isServer;
	private static boolean isInit = false;
	private static boolean isStarted = false;
	
	public static final String VERSION = "0.1.1";
	public static final String MOD_ID = "syncmatica";
	public static final String SERVER_PATH = "."+File.separator+"syncmatics";
	public static final String CLIENT_PATH = "."+File.separator+"schematics"+File.separator+".sync";
	
	private static CommunicationManager comms;
	private static IFileStorage data;
	private static SyncmaticManager schematics;
	
	public static String getSchematicPath(ServerPlacement p) {
		if (isServer) {
			return getStoragePath()+File.separator+p.getHash().toString()+".litematic";
		} else {
			return getStoragePath()+File.separator+p.getName()+".litematic";
		}
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
		return (!partnerVersion.equals("0.0.1"));
	}
	
	public static void saveServer() {
		JsonObject obj = new JsonObject();
		JsonArray arr = new JsonArray();
		
		for (ServerPlacement p : schematics.getAll()) {
			arr.add(p.toJson());
		}
		
		obj.add("placements", arr);
		getConfigFolder().mkdirs();
		File f = new File(getConfigFolder(), "placements.json");
		
		FileWriter writer = null;
        try {
        	writer = new FileWriter(f);
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	public static void loadServer() {
		File f = new File(getConfigFolder(), "placements.json");
        if (f != null && f.exists() && f.isFile() && f.canRead()) {
        	JsonElement element = null;
            try {
                JsonParser parser = new JsonParser();
                FileReader reader = new FileReader(f);

                element = parser.parse(reader);
                reader.close();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
            	JsonObject obj = element.getAsJsonObject();
            	if (!obj.has("placements")) {return;}
            	JsonArray arr = obj.getAsJsonArray("placements");
            	for (JsonElement elem : arr) {
            		ServerPlacement p = ServerPlacement.fromJson(elem.getAsJsonObject());
            		schematics.addPlacement(p);
            	}
            	
            } catch (IllegalStateException e) {
            	e.printStackTrace();
            } catch (NullPointerException e) {
            	e.printStackTrace();
            }
        }
	}
	
	private static File getConfigFolder() {
		return new File(new File("."), "config"+File.separator+MOD_ID);
	}
	
}
