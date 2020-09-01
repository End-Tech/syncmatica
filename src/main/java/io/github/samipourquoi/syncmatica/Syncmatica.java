package io.github.samipourquoi.syncmatica;

import java.io.File;

public class Syncmatica {
	
	public static boolean isServer;
	public static final String SERVER_PATH = "."+File.separator+"syncmatics";
	public static final String CLIENT_PATH = "."+File.separator+"schematics"+File.separator+".sync";
	
	public static String getSyncmaticaPlacementPath(String fileName) {
		return getStoragePath()+File.separator+fileName;
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
		file.mkdir();
		isServer = true;
	}

	public static void initClient() {
		File file = new File(CLIENT_PATH);
		file.mkdirs();
		isServer = false;
	}
}
