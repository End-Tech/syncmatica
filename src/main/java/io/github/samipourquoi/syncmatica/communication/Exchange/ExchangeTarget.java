package io.github.samipourquoi.syncmatica.communication.Exchange;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;

// since Client/Server PlayNetworkHandler are 2 different classes but I want to use exchanges
// on both without having to recode them individually I have an adapter class here

public class ExchangeTarget {
	private ClientPlayNetworkHandler server = null; 
	private ServerPlayNetworkHandler client = null;
	
	public ExchangeTarget(ClientPlayNetworkHandler server) {
		this.server = server;
	}
	
	public ExchangeTarget(ServerPlayNetworkHandler client) {
		this.client = client;
	}
	
	public void sendPacket(Packet<?> packet) {
		if (server==null) {
			client.sendPacket(packet);
		} else {
			server.sendPacket(packet);
		}
	}
	
	// to properly sort into hashTables according to the target
	@Override
	public int hashCode() {
		if (server==null) {
			return client.hashCode();
		} else {
			return server.hashCode();
		}
	}
	
	// to properly sort into hashTables according to the target
	@Override
	public boolean equals(Object o) {
		if (server==null) {
			return client.equals(o);
		} else {
			return server.equals(o);
		}
	}
}
