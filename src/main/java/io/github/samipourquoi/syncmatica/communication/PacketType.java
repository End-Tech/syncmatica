package io.github.samipourquoi.syncmatica.communication;

import net.minecraft.util.Identifier;

public enum PacketType {
	@Deprecated
	REGISTER("sycnmatica:register"), // a single packet design doesn't make any sense
	// and has the potential to permanently damage the server to syncmatica users since the
	// downloading of a syncmatic is blocking the bandwidth of the connection
	// leading to a potential disconnection due to no response being made
	// with larger litematics
	// this potentially leaves the server open for attacks that upload very large litematics from
	// multiple connections
	// additionally it will be hard to move server syncmatics since the entire litematic
	// will have to be resend each time a syncmatic is moved
	// therefore a split system will be used
	// also buffering syncmatics over several sessions on the same server will become impossible
	// for clients
	
	REGISTER_METADATA("syncmatica:register_metadata"),
	// one packet will be responsible for sending the entire meta data of a syncmatic
	// it marks the creation of a syncmatic - for now it also is responsible
	// for changing the syncmatic server and client side
	
	REQUEST_LITEMATIC("syncmatica:request_download"),
	// another group of packets will be responsible for downloading the entire
	// litematic starting with a download request
	
	SEND_LITEMATIC("syncmatica:send_litematic"),
	// a packet responsible for sending a bit of a syncmatic (32kbits to be precise (half of a TCP/IP packets size))
	
	RECEIVED_LITEMATIC("syncmatica:received_litematic"),
	// a packet responsible for triggering another send for a litematic
	// by waiting until a response is send I hope we can ensure 
	// that we do not overwhelm the clients connection to the server
	
	FINISHED_LITEMATIC("syncmatica:finished_litematic");
	// a packet responsible for marking the end of a litematic
	// transmission
	
	public final Identifier IDENTIFIER;

	PacketType(String id) {
		this.IDENTIFIER = new Identifier(id);
	}
	
	public static boolean containsIdentifier(Identifier id) {
		for (PacketType p : PacketType.values()) {
			if (id.equals(p)) {
				return true;
			}
		}
		return false;
	}
}
