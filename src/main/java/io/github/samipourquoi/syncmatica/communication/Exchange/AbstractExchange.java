package io.github.samipourquoi.syncmatica.communication.Exchange;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;

public abstract class AbstractExchange implements Exchange {

	private boolean success = false;
	private boolean finished = false;
	private ExchangeTarget partner;
	private CommunicationManager manager;
	
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
	public void close() {
		finished = true;
		success = false;
		onClose();
	}
	
	protected void onClose() {
		return;
	}
	
	protected void succeed() {
		finished = true;
		success = false;
		onClose();
	}
	
}
