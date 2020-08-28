package io.github.samipourquoi.syncmatica;

import java.io.File;

public class Syncmatica {
	public static void initServer() {
		File file = new File("./syncmatics");
		file.mkdir();
	}

	public static void initClient() {
		File file = new File("./schematics");
		file.mkdir();
	}
}
