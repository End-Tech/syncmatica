package io.github.samipourquoi.syncmatica;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;

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
