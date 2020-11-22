package io.github.samipourquoi.syncmatica.communication.exchange;

import java.io.File;
import java.io.FileNotFoundException;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import io.github.samipourquoi.syncmatica.RedirectFileStorage;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ShareLitematicExchange extends AbstractExchange {
	
	private final SchematicPlacement schem;
	private final ServerPlacement toShare;
	private final File toUpload;
	
	public ShareLitematicExchange(SchematicPlacement schem, ExchangeTarget partner, CommunicationManager manager) {
		super(partner, manager);
		this.schem = schem;
		toShare = LitematicManager.getInstance().syncmaticFromSchematic(schem);
		toUpload = schem.getSchematicFile();
	}

	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.REQUEST_LITEMATIC.IDENTIFIER)
				||id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)
				||id.equals(PacketType.CANCEL_SHARE.IDENTIFIER)) {
			return checkUUID(packetBuf, toShare.getId());
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
			return;
		}
		if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
			RedirectFileStorage redirect = (RedirectFileStorage)Syncmatica.getFileStorage();
			redirect.addRedirect(toUpload);
			LitematicManager.getInstance().renderSyncmatic(toShare, schem, false);
			Syncmatica.getSyncmaticManager().addPlacement(toShare);
			return;
		}
		if (id.equals(PacketType.CANCEL_SHARE.IDENTIFIER)) {
			close(false);
		}
	}

	@Override
	public void init() {
		((ClientCommunicationManager) getManager()).setSharingState(toShare, true);
		Syncmatica.getSyncmaticManager().updateServerPlacement(toShare);
		getManager().sendMetaData(toShare, getPartner());
	}
	
	@Override
	public void onClose() {
		((ClientCommunicationManager) getManager()).setSharingState(toShare, false);
	}

	@Override
	protected void sendCancelPacket() {}
}
