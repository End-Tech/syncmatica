package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.Exchange.ShareLitematicExchange;
import io.github.samipourquoi.syncmatica.litematica_mixin.MixinWidgetSchematicPlacement;

public class ButtonListenerShare implements IButtonActionListener {
	
	private final MixinWidgetSchematicPlacement parentWidget;
	
	public ButtonListenerShare(MixinWidgetSchematicPlacement parentWidget) {
		this.parentWidget = parentWidget;
	}

	@Override
	public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
		SchematicPlacement schem = parentWidget.getPlacement();
		ClientCommunicationManager comms = (ClientCommunicationManager) Syncmatica.getCommunicationManager();
		ExchangeTarget server = comms.getServer();
		ShareLitematicExchange ex = new ShareLitematicExchange(schem, server, comms);
		comms.startExchange(ex);
	}

}
