package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.exchange.ShareLitematicExchange;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;

public class ButtonListenerShare implements IButtonActionListener {
	
	private final SchematicPlacement schem;
	
	public ButtonListenerShare(SchematicPlacement placement) {
		schem = placement;
	}

	@Override
	public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
		if (LitematicManager.getInstance().isSyncmatic(schem)) {
			return;
		}
		button.setEnabled(false);
		Context con = LitematicManager.getInstance().getActiveContext();
		ExchangeTarget server = ((ClientCommunicationManager)con.getCommunicationManager()).getServer();
		ShareLitematicExchange ex = new ShareLitematicExchange(schem, server, con);
		con.getCommunicationManager().startExchange(ex);
	}

}
