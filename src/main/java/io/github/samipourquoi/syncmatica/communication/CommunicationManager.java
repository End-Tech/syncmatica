package io.github.samipourquoi.syncmatica.communication;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.communication.exchange.DownloadExchange;
import io.github.samipourquoi.syncmatica.communication.exchange.Exchange;
import io.github.samipourquoi.syncmatica.communication.exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class CommunicationManager {
	protected final Collection<ExchangeTarget> broadcastTargets;
	protected final Map<ExchangeTarget, Collection<Exchange>> openExchange;
	
	protected final Map<UUID,Boolean> downloadState;
	
	protected final IFileStorage fileStorage;
	protected final SyncmaticManager schematicManager;
	
	protected static final BlockRotation[] rotOrdinals = BlockRotation.values();
	protected static final BlockMirror[] mirOrdinals = BlockMirror.values();
	
	public CommunicationManager(IFileStorage storage, SyncmaticManager manager) {
		fileStorage = storage;
		broadcastTargets = new ArrayList<>();
		openExchange = new HashMap<>();
		downloadState = new HashMap<>();
		storage.setCommunitcationManager(this);
		schematicManager = manager;
	}
	
	public boolean handlePacket(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		return PacketType.containsIdentifier(id);
	}
	
	public void onPacket(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf) {
		// TODO: Timeout Exchanges
		Exchange handler = null;
		// id is one of the syncmatica packet types
		if (!handlePacket(source, id, packetBuf)) {
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
		putMetaData(metaData, buf);
		target.sendPacket(PacketType.REGISTER_METADATA.IDENTIFIER, buf);
	}
	
	public void putMetaData(ServerPlacement metaData, PacketByteBuf buf) {
		buf.writeUuid(metaData.getId());
		
		buf.writeString(sanitizeFileName(metaData.getName()));
		buf.writeUuid(metaData.getHash());
		
		buf.writeBlockPos(metaData.getPosition());
		buf.writeString(metaData.getDimension());
		// one of the rare use cases for ordinal
		// transmitting the information of a non modifying enum to another
		// instance of this application with no regard to the persistence
		// of the ordinal values over time
		buf.writeInt(metaData.getRotation().ordinal());
		buf.writeInt(metaData.getMirror().ordinal());
	}
	
	public ServerPlacement receiveMetaData(PacketByteBuf buf) {
		UUID id = buf.readUuid();
		
		String fileName = sanitizeFileName(buf.readString(32767));
		UUID hash = buf.readUuid();
		ServerPlacement placement =  new ServerPlacement(id, fileName, hash);
		
		BlockPos pos = buf.readBlockPos();
		String dimensionId = buf.readString(32767);
		BlockRotation rot = rotOrdinals[buf.readInt()];
		BlockMirror mir = mirOrdinals[buf.readInt()];
		placement.move(dimensionId, pos, rot, mir);
		
		return placement;
	}
	
	public void download(ServerPlacement syncmatic, ExchangeTarget source) throws NoSuchAlgorithmException, IOException {
		if (!fileStorage.getLocalState(syncmatic).isReadyForDownload()) {
			// forgot a negation here
			throw new IllegalArgumentException(syncmatic.toString()+" is not ready for download local state is: "+fileStorage.getLocalState(syncmatic).toString());
		}
		File toDownload = fileStorage.createLocalLitematic(syncmatic);
		Exchange downloadExchange = new DownloadExchange(syncmatic, toDownload, source, this);
		setDownloadState(syncmatic, true);
		startExchange(downloadExchange);
		schematicManager.updateServerPlacement(syncmatic);
	}

	public void setDownloadState(ServerPlacement syncmatic, boolean b) {
		downloadState.put(syncmatic.getHash(), b);
		schematicManager.updateServerPlacement(syncmatic);
	}
	
	public boolean getDownloadState(ServerPlacement syncmatic) {
		return downloadState.getOrDefault(syncmatic.getHash(), false);
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
	
	
	// taken from stackoverflow
	final static int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
	final static String illegalPatterns = "(^(con|prn|aux|nul|com[0-9]|lpt[0-9])(\\..*)?$)|(^\\.\\.*$)";
	
	static {
		Arrays.sort(illegalChars);
	}
	
	private static String sanitizeFileName(String badFileName) {
		StringBuilder sanitized = new StringBuilder();
	    int len = badFileName.codePointCount(0, badFileName.length());
	    
	    for (int i=0; i<len; i++) {
	      int c = badFileName.codePointAt(i);
	      if (Arrays.binarySearch(illegalChars, c) < 0) {
	    	  sanitized.appendCodePoint(c);
	    	  if (sanitized.length() == 255) { //make sure .length stays below 255
	    		  break;
	    	  }
	      }
	    }
	    // ^ sanitizes unique characters
	    // v sanatizes entire patterns
		return sanitized.toString().replaceAll(illegalPatterns, "_");
	}
	
}
