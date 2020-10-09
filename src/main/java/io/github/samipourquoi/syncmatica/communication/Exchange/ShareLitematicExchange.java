package io.github.samipourquoi.syncmatica.communication.Exchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ShareLitematicExchange extends AbstractExchange {
	
	private final ServerPlacement toShare;
	private final File toUpload;
	
	public ShareLitematicExchange(ServerPlacement placement, File litematic, ExchangeTarget partner, CommunicationManager manager) {
		super(partner, manager);
		toShare = placement;
		toUpload = litematic;
	}

	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REQUEST_LITEMATIC.IDENTIFIER)||id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			byte[] uuidByte = new byte[16];
			for (int i = 0; i<16; i++) {
				// getByte does not progress the pointer
				uuidByte[i] = packetBuf.getByte(i);
			}
			return (UUID.nameUUIDFromBytes(uuidByte) == toShare.getId());
		}
		return false;
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REQUEST_LITEMATIC.IDENTIFIER)) {
			packetBuf.readUuid();
			UploadExchange upload = null;
			try {
				upload = new UploadExchange(toShare, toUpload, getPartner(), getManager());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (upload == null) {
				return;
			}
			getManager().startExchange(upload);
		}
		if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			// TODO: change litematic to syncmatic, register as such & render it
		}
	}

	@Override
	public void init() {
		getManager().sendMetaData(toShare, getPartner());
	}
}
