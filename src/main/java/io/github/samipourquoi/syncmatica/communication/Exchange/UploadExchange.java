package io.github.samipourquoi.syncmatica.communication.Exchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.SyncmaticaLitematicaFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacement;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

// uploading part of transmit data exchange
// pairs with Download Exchange

public class UploadExchange extends AbstractExchange {
	
	private static final int BUFFER_SIZE = 32768;
	
	private final SyncmaticaServerPlacement toUpload;
	private final InputStream inputStream;
	private byte[] buffer = new byte[BUFFER_SIZE];
	
	public UploadExchange(SyncmaticaServerPlacement syncmatic, ExchangeTarget partner, CommunicationManager manager) throws FileNotFoundException {
		super(partner, manager);
		toUpload = syncmatic;
		File uploadFile = SyncmaticaLitematicaFileStorage.getLocalLitematic(syncmatic);
		inputStream = new FileInputStream(uploadFile);
	}

	@Override
	public boolean checkPackage(CustomPayloadS2CPacket packet) {
		Identifier ident = packet.getChannel();
		if (ident == PacketType.RECEIVED_LITEMATIC.IDENTIFIER) {
			PacketByteBuf buf = packet.getData();
			byte[] uuidByte = new byte[16];
			for (int i = 0; i<16; i++) {
				// getByte does not progress the pointer
				uuidByte[i] = buf.getByte(i);
			}
			return (UUID.nameUUIDFromBytes(uuidByte) == toUpload.getId());
		}
		return false;
	}

	@Override
	public void handle(CustomPayloadS2CPacket packet) {
		packet.getData().readUuid(); // uncertain if the data has to be consumed
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
		CustomPayloadS2CPacket dataPacket = new CustomPayloadS2CPacket(PacketType.SEND_LITEMATIC.IDENTIFIER, packetByteBuf);
		getPartner().sendPacket(dataPacket);
	}

	private void sendFinish() {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(toUpload.getId());
		CustomPayloadS2CPacket finishPacket = new CustomPayloadS2CPacket(PacketType.FINISHED_LITEMATIC.IDENTIFIER, packetByteBuf);
		getPartner().sendPacket(finishPacket);
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
