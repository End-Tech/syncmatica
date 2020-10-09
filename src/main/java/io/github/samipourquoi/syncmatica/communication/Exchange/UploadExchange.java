package io.github.samipourquoi.syncmatica.communication.Exchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

// uploading part of transmit data exchange
// pairs with Download Exchange

public class UploadExchange extends AbstractExchange {
	
	private static final int BUFFER_SIZE = 32768;
	
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
		if (id.equals(PacketType.RECEIVED_LITEMATIC.IDENTIFIER)) {
			byte[] uuidByte = new byte[16];
			for (int i = 0; i<16; i++) {
				// getByte does not progress the pointer
				uuidByte[i] = packetBuf.getByte(i);
			}
			return (UUID.nameUUIDFromBytes(uuidByte) == toUpload.getId());
		}
		return false;
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		packetBuf.readUuid(); // uncertain if the data has to be consumed
		send();
	}

	private void send() {
		// might fail when an empty file is attempted to be transmitted
		int bytesRead = -1;
		try {
			bytesRead = inputStream.read(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
	
}
