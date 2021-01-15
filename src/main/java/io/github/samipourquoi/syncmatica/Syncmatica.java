package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import net.minecraft.util.Identifier;

// could probably turn this into a singleton

public class Syncmatica {
	
	public static final String VERSION = "0.2.0";
	public static final String MOD_ID = "syncmatica";
	
	private static final String SERVER_PATH = "."+File.separator+"syncmatics";
	private static final String CLIENT_PATH = "."+File.separator+"schematics"+File.separator+"sync";
	
	public static final Identifier CLIENT_CONTEXT = new Identifier("syncmatica:client_context");
	public static final Identifier SERVER_CONTEXT = new Identifier("syncmatica:server_context");
	
	private static Map<Identifier,Context> contexts = null;
	
	public static void initServer(CommunicationManager comms, IFileStorage fileStorage, SyncmaticManager schematics) {
		Context serverContext = new Context(fileStorage, comms, schematics, true, new File(SERVER_PATH));
		init(serverContext, SERVER_CONTEXT);
	}

	public static void initClient(CommunicationManager comms, IFileStorage fileStorage, SyncmaticManager schematics) {
		Context clientContext = new Context(fileStorage, comms, schematics, false, new File(CLIENT_PATH));
		init(clientContext, CLIENT_CONTEXT);
	}
	
	public static Context getContext(Identifier id) {
		return contexts.get(id);
	}
	
	private static void init(Context con, Identifier contextId) {
		if (contexts == null) {
			contexts = new HashMap<>();
		}
		if (!contexts.containsKey(contextId)) {
			contexts.put(contextId, con);
		}
	}
	
	public static void shutdown() {
		if (contexts != null) {
			for (Context con: contexts.values()) {
				if (con.isStarted()) {
					con.shutdown();
				}
			}
		}
		deinit();
	}
	
	private static void deinit() {
		contexts = null;
	}
	
}
