package io.github.samipourquoi.syncmatica.litematica.gui;

import org.apache.logging.log4j.LogManager;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.exchange.ShareLitematicExchange;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;

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
		LogManager.getLogger(ClientPlayNetworkHandler.class).info("Started sharing litematic");
		ClientCommunicationManager comms = (ClientCommunicationManager) Syncmatica.getCommunicationManager();
		ExchangeTarget server = comms.getServer();
		ShareLitematicExchange ex = new ShareLitematicExchange(schem, server, comms);
		comms.startExchange(ex);
	}

}
