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
	
	CANCEL_SHARE("syncmatica:cancel_share"),
	// send to a client when a share failed
	// the client can cancel the upload or upon finishing send a removal packet
	
	REQUEST_LITEMATIC("syncmatica:request_download"),
	// another group of packets will be responsible for downloading the entire
	// litematic starting with a download request
	
	SEND_LITEMATIC("syncmatica:send_litematic"),
	// a packet responsible for sending a bit of a litematic (16kbits to be precise (half of what minecraft can send in one packet at most))
	
	RECEIVED_LITEMATIC("syncmatica:received_litematic"),
	// a packet responsible for triggering another send for a litematic
	// by waiting until a response is send I hope we can ensure 
	// that we do not overwhelm the clients connection to the server
	
	FINISHED_LITEMATIC("syncmatica:finished_litematic"),
	// a packet responsible for marking the end of a litematic
	// transmission
	
	CANCEL_LITEMATIC("syncmatica:cancel_litematic"),
	// a packet responsible for cancelling an ongoing upload/download
	// will be sent in several cases - upon errors mostly
	
	REGISTER_VERSION("syncmatica:register_version"),
	// this packet will be send to the client when it joins the server
	// upon receiving this packet the client will check the server version
	// initializes syncmatica on the clients end
	// if it can function with the version on the server then it will respond with a version of its own 
	// if the server can handle the client version the server will send
	
	CONFIRM_USER("syncmatica:confirm_user");
	// the confirm user packet
	// send after a successful version exchange
	// fully starts up syncmatica on the clients end
	
	public final Identifier IDENTIFIER;

	PacketType(String id) {
		this.IDENTIFIER = new Identifier(id);
	}
	
	public static boolean containsIdentifier(Identifier id) {
		for (PacketType p : PacketType.values()) {
			if (id.equals(p.IDENTIFIER)) { // this took I kid you not 4-5 hours to find
				return true;
			}
		}
		return false;
	}
}
