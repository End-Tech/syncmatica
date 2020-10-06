package io.github.samipourquoi.syncmatica.communication.Exchange;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

// an exchange represents a portion of a communication with a specific goal 
// that stretches across multiple packages
// an exchange has exactly 2 participants since one of the participants is implied
// to be the vm holding this exchange only the partner has to be noted
// another thing of note is that an Exchange is only one half of the entire exchange
// thus only encompasses half of the entire behavior of an exchange
// e.g. there could be a class receiveData and a class sendData creating
// the 2 halves of a transmit data exchange



public interface Exchange {
	
	//TODO: Timeout stuff
	
	// uniquely identifies the partner of this exchange
	public ExchangeTarget getPartner();
	
	// in case an exchange starts another exchange they need to be able to reach for
	// the manager
	public CommunicationManager getManager();
	
	// looks into the received packet and returns
	// whether this exchange handles the packet or not
	// this test should have no side effects.
	// doesn't handle packets directly 
	public boolean checkPackage(Identifier id, PacketByteBuf packetBuf);
	
	// handles the data of this specific packet
	public void handle(Identifier id, PacketByteBuf packetBuf);
	
	// marks an exchange that has terminated
	public boolean isFinished();
	
	// marks a successfully finished exchange
	public boolean isSuccessful();
	
	// marks an external unsuccessful close
	public void close();
	
	// initializes the actual Exchange
	public void init();
	
}
