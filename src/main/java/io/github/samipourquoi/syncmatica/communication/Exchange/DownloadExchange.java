package io.github.samipourquoi.syncmatica.communication.Exchange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.DigestOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class DownloadExchange extends AbstractExchange {
	
	private final ServerPlacement toDownload;
	private final OutputStream outputStream;
	private final MessageDigest md5;
	
	public DownloadExchange(ServerPlacement syncmatic, File downloadFile, ExchangeTarget partner, CommunicationManager manager) throws IOException, NoSuchAlgorithmException {
		super(partner, manager);
		OutputStream os = new FileOutputStream(downloadFile);
		toDownload = syncmatic;
		md5 = MessageDigest.getInstance("MD5");
		outputStream = new DigestOutputStream(os, md5);
	}

	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.SEND_LITEMATIC.IDENTIFIER)
				||id.equals(PacketType.FINISHED_LITEMATIC.IDENTIFIER)
				||id.equals(PacketType.CANCEL_LITEMATIC.IDENTIFIER)) {
			return checkUUID(packetBuf, toDownload.getId());
		}
		return false;
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		packetBuf.readUuid(); //skips the UUID
		if (id.equals(PacketType.SEND_LITEMATIC.IDENTIFIER)) {
			int size = packetBuf.readInt();
				try {
					packetBuf.readBytes(outputStream, size);
				} catch (IOException e) {
					close(true);
					e.printStackTrace();
					return;
				}
				PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
				packetByteBuf.writeUuid(toDownload.getId());
				getPartner().sendPacket(PacketType.RECEIVED_LITEMATIC.IDENTIFIER, packetByteBuf);
				return;
		}
		if (id.equals(PacketType.FINISHED_LITEMATIC.IDENTIFIER)) {
			try {
				outputStream.flush();
			} catch (IOException e) {
				close(false);
				e.printStackTrace();
				return;
			}
			UUID downloadHash = UUID.nameUUIDFromBytes(md5.digest());
			if (downloadHash.equals(toDownload.getHash())) {
				succeed();
			} else {
				// no need to notify partner since exchange is closed on partner side
				close(false);
			}
			return;
		}
		if (id.equals(PacketType.CANCEL_LITEMATIC.IDENTIFIER)) {
			close(false);
		}
	}

	@Override
	public void init() {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(toDownload.getId());
		getPartner().sendPacket(PacketType.REQUEST_LITEMATIC.IDENTIFIER, packetByteBuf);
	}
	
	@Override
	protected void onClose() {
		getManager().setDownloadState(toDownload, false);
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void sendCancelPacket() {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeUuid(toDownload.getId());
		getPartner().sendPacket(PacketType.CANCEL_LITEMATIC.IDENTIFIER, packetByteBuf);
	}
	
	public ServerPlacement getPlacement() {
		return toDownload;
	}

}
