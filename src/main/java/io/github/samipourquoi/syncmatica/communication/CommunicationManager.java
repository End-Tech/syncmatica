package io.github.samipourquoi.syncmatica.communication;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.FileStorage;
import io.github.samipourquoi.syncmatica.SchematicManager;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.Exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class CommunicationManager {
	protected final Collection<ExchangeTarget> broadcastTargets = new ArrayList<>();
	protected final Map<ExchangeTarget, Collection<Exchange>> openExchange = new HashMap<>();
	
	private final Map<ServerPlacement,Boolean> downloadState = new HashMap<>();
	
	protected final FileStorage fileStorage;
	protected final SchematicManager schematicManager;
	
	protected static final BlockRotation[] rotOrdinals = BlockRotation.values();
	protected static final BlockMirror[] mirOrdinals = BlockMirror.values();
	
	public CommunicationManager(FileStorage storage, SchematicManager manager) {
		fileStorage = storage;
		storage.setCommunitcationManager(this);
		schematicManager = manager;
	}
	
	public void onPacket(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		// TODO: Timeout Exchanges
		Exchange handler = null;
		// id is one of the syncmatica packet types
		if (!PacketType.containsIdentifier(id)) {
			return;
		}
		Collection<Exchange> potentialMessageTarget = openExchange.get(source);
		if (potentialMessageTarget != null) {
			for (Exchange target: potentialMessageTarget) {
				if (target.checkPacket(id, packetBuf)) {
					target.handle(id, packetBuf);
					handler = target;
					break;
				}
			}
		}
		if (handler == null) {
			handle(source, id, packetBuf);
		} else if (handler.isFinished()){
			 potentialMessageTarget.remove(handler);
			 handleExchange(handler);
		}
	}
	
	// will get called for every packet not handled by an exchange
	protected abstract void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf);
	
	// will get called for every finished exchange (successful or not)
	protected abstract void handleExchange(Exchange exchange);
	
	public void sendMetaData(ServerPlacement metaData, ExchangeTarget target) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeUuid(metaData.getId());
		
		buf.writeString(metaData.getFileName());
		buf.writeBytes(metaData.getHash());
		
		buf.writeBlockPos(metaData.getPosition());
		buf.writeString(metaData.getDimension());
		// one of the rare use cases for ordinal
		// transmitting the information of a non modifying enum to another
		// instance of this application with no regard to the persistence
		// of the ordinal values over time
		buf.writeInt(metaData.getRotation().ordinal());
		buf.writeInt(metaData.getMirror().ordinal());
		target.sendPacket(PacketType.REGISTER_METADATA.IDENTIFIER, buf);
	}
	
	public ServerPlacement receiveMetaData(PacketByteBuf buf) {
		UUID id = buf.readUuid();
		
		String fileName = buf.readString();
		byte[] hash = new byte[16];
		buf.readBytes(hash);	
		ServerPlacement placement = new ServerPlacement(id, fileName, hash);
		
		BlockPos pos = buf.readBlockPos();
		String dimensionId = buf.readString();
		BlockRotation rot = rotOrdinals[buf.readInt()];
		BlockMirror mir = mirOrdinals[buf.readInt()];
		placement.move(dimensionId, pos, rot, mir);
		
		return placement;
	}
	
	public void download(ServerPlacement syncmatic, ExchangeTarget source) throws NoSuchAlgorithmException, IOException {
		if (fileStorage.getLocalState(syncmatic).isReadyForDownload()) {
			throw new IllegalArgumentException(syncmatic.toString()+" is not ready for download");
		}
		File toDownload = fileStorage.createLocalLitematic(syncmatic);
		Exchange downloadExchange = new DownloadExchange(syncmatic, toDownload, source, this);
		setDownloadState(syncmatic, true);
		startExchange(downloadExchange);
	}

	public void setDownloadState(ServerPlacement syncmatic, boolean b) {
		downloadState.put(syncmatic, b);
	}
	
	public boolean getDownloadState(ServerPlacement syncmatic) {
		return downloadState.getOrDefault(syncmatic, false);
	}

	public void startExchange(Exchange newExchange) {
		if (!broadcastTargets.contains(newExchange.getPartner())) {
			throw new IllegalArgumentException(newExchange.getPartner().toString()+" is not a valid ExchangeTarget");
		}
		startExchangeUnchecked(newExchange);
	}
	
	protected void startExchangeUnchecked(Exchange newExchange) {
		openExchange.computeIfAbsent(newExchange.getPartner(), (k) -> new ArrayList<>()).add(newExchange);
		newExchange.init();
	}
	
}
