package io.github.samipourquoi.syncmatica.communication.Exchange;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

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
	
	// this application exclusively communicates in CustomPayLoad packets
	// this class handles the sending of either S2C or C2S packets
	public void sendPacket(Identifier id, PacketByteBuf packetBuf) {
		if (server==null) {
			CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(id, packetBuf);
			client.sendPacket(packet);
		} else {
			CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(id, packetBuf);
			server.sendPacket(packet);
		}
	}
	
	// removed equals code due to issues with Collection.contains
}
