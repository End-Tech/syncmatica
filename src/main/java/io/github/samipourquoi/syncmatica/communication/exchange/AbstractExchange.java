package io.github.samipourquoi.syncmatica.communication.exchange;

import java.util.UUID;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import net.minecraft.network.PacketByteBuf;

public abstract class AbstractExchange implements Exchange {

	private boolean success = false;
	private boolean finished = false;
	private final ExchangeTarget partner;
	private final Context context;
	
	public AbstractExchange(ExchangeTarget partner, Context con) {
		this.partner = partner;
		this.context = con;
	}
	
	@Override
	public ExchangeTarget getPartner() {
		return partner;
	}
	
	@Override
	public Context getContext() {
		return context;
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
	
	public CommunicationManager getManager() {
		return context.getCommunicationManager();
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
