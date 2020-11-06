package io.github.samipourquoi.syncmatica.communication.Exchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

// uploading part of transmit data exchange
// pairs with Download Exchange

public class UploadExchange extends AbstractExchange {
	
	// The maximum buffer size for CustomPayloadPackets is actually 32767
	// so 32768 is a bad value to send - thus adjusted it to 16384 - exactly halfed
	private static final int BUFFER_SIZE = 16384;
	
	private final ServerPlacement toUpload;
	private final InputStream inputStream;
	private byte[] buffer = new byte[BUFFER_SIZE];
	
	public UploadExchange(ServerPlacement syncmatic, File uploadFile,ExchangeTarget partner, CommunicationManager manager) throws FileNotFoundException {
		super(partner, manager);
		toUpload = syncmatic;
		inputStream = new FileInputStream(uploadFile);
	}

	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.RECEIVED_LITEMATIC.IDENTIFIER)
				||id.equals(PacketType.CANCEL_LITEMATIC.IDENTIFIER)) {
			return checkUUID(packetBuf, toUpload.getId());
		}
		return false;
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		
		packetBuf.readUuid(); // uncertain if the data has to be consumed
		if (id.equals(PacketType.RECEIVED_LITEMATIC.IDENTIFIER)) {
			send();
		}
		if (id.equals(PacketType.CANCEL_LITEMATIC.IDENTIFIER)) {
			close(false);
		}
	}

	private void send() {
		// might fail when an empty file is attempted to be transmitted
		int bytesRead = -1;
		try {
			bytesRead = inputStream.read(buffer);
		} catch (IOException e) {
			close(true);
			e.printStackTrace();
			return;
		}
		if (bytesRead == -1) {
			sendFinish();
		} else {
			sendData(bytesRead);
		}
	}

	private void sendData(int bytesRead) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(toUpload.getId());
		packetByteBuf.writeInt(bytesRead);
		packetByteBuf.writeBytes(buffer, 0, bytesRead);
		getPartner().sendPacket(PacketType.SEND_LITEMATIC.IDENTIFIER, packetByteBuf);
	}

	private void sendFinish() {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(toUpload.getId());
		getPartner().sendPacket(PacketType.FINISHED_LITEMATIC.IDENTIFIER, packetByteBuf);
		succeed();
	}

	@Override
	public void init() {
		send();
	}
	
	@Override
	protected void onClose() {
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void sendCancelPacket() {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(toUpload.getId());
		getPartner().sendPacket(PacketType.CANCEL_LITEMATIC.IDENTIFIER, packetByteBuf);
	}
	
}