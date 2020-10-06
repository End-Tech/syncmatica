package io.github.samipourquoi.syncmatica.communication.Exchange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.DigestOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.SyncmaticaLitematicaFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacement;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class DownloadExchange extends AbstractExchange {
	
	private final SyncmaticaServerPlacement toDownload;
	private final OutputStream outputStream;
	private final MessageDigest md5;
	
	public DownloadExchange(SyncmaticaServerPlacement syncmatic, ExchangeTarget partner, CommunicationManager manager) throws IOException, NoSuchAlgorithmException {
		super(partner, manager);
		File fileToDownload = SyncmaticaLitematicaFileStorage.createLocalLitematic(syncmatic);
		OutputStream os = new FileOutputStream(fileToDownload);
		toDownload = syncmatic;
		md5 = MessageDigest.getInstance("MD5");
		outputStream = new DigestOutputStream(os, md5);
	}

	@Override
	public boolean checkPackage(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.SEND_LITEMATIC.IDENTIFIER)||id.equals(PacketType.FINISHED_LITEMATIC.IDENTIFIER)) {
			byte[] uuidByte = new byte[16];
			for (int i = 0; i<16; i++) {
				// getByte does not progress the pointer
				uuidByte[i] = packetBuf.getByte(i);
			}
			return (UUID.nameUUIDFromBytes(uuidByte) == toDownload.getId());
		}
		return false;
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		packetBuf.readUuid(); //skips the UUID
		if (id.equals(PacketType.SEND_LITEMATIC.IDENTIFIER)) {
			int size = packetBuf.readInt();
			byte[] data = packetBuf.readByteArray(size);
				try {
					outputStream.write(data);
				} catch (IOException e) {
					this.close();
					throw new RuntimeException(e);
				}
				PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
				packetByteBuf.writeUuid(toDownload.getId());
				getPartner().sendPacket(PacketType.RECEIVED_LITEMATIC.IDENTIFIER, packetByteBuf);
		}
		if (id.equals(PacketType.FINISHED_LITEMATIC.IDENTIFIER)) {
			byte[] downloadHash = md5.digest();
			byte[] placementHash = toDownload.getHash();
			try {
				outputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (Arrays.equals(downloadHash, placementHash)) {
				succeed();
			} else {
				close();
			}
		}
	}

	@Override
	public void init() {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(toDownload.getId());
		getPartner().sendPacket(PacketType.REQUEST_LITEMATIC.IDENTIFIER, packetByteBuf);
	}

}
