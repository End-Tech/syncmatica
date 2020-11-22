package io.github.samipourquoi.syncmatica.communication.exchange;

import java.util.UUID;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import net.minecraft.network.PacketByteBuf;

public abstract class AbstractExchange implements Exchange {

	private boolean success = false;
	private boolean finished = false;
	private final ExchangeTarget partner;
	private final CommunicationManager manager;
	
	public AbstractExchange(ExchangeTarget partner, CommunicationManager manager) {
		this.partner = partner;
		this.manager = manager;
	}
	
	@Override
	public ExchangeTarget getPartner() {
		return partner;
	}
	
	@Override
	public CommunicationManager getManager() {
		return manager;
	}
	
	@Override
	public boolean isFinished() {
		return finished;
	}
	
	@Override
	public boolean isSuccessful() {
		return success;
	}
	
	@Override
	public void close(boolean notifyPartner) {
		finished = true;
		success = false;
		onClose();
		if (notifyPartner) {
			sendCancelPacket();
		}
	}
	
	protected abstract void sendCancelPacket();

	protected void onClose() {
		return;
	}
	
	protected void succeed() {
		finished = true;
		success = true;
		// Ctrl+C Ctrl+V and forget to adapt the success state - typical
		onClose();
	}
	
	protected static boolean checkUUID(PacketByteBuf sourceBuf, UUID targetId) {
		int r = sourceBuf.readerIndex();
		UUID sourceId = sourceBuf.readUuid();
		sourceBuf.readerIndex(r);
		return sourceId.equals(targetId);
	}
	
}
