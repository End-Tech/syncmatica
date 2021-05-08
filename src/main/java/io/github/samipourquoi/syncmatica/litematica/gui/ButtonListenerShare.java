package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.exchange.ShareLitematicExchange;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;

public class ButtonListenerShare implements IButtonActionListener {

    private final SchematicPlacement schem;
    private final GuiBase messageDisplay;

    public ButtonListenerShare(final SchematicPlacement placement, final GuiBase messageDisplay) {
        schem = placement;
        this.messageDisplay = messageDisplay;
    }

    @Override
    public void actionPerformedWithButton(final ButtonBase button, final int mouseButton) {
        if (LitematicManager.getInstance().isSyncmatic(schem)) {
            return;
        }
        if (!GuiBase.isShiftDown()) {
            messageDisplay.addMessage(Message.MessageType.ERROR, "syncmatica.error.share_without_shift");
            return;
        }
        if (schem.isRegionPlacementModified()) {
            messageDisplay.addMessage(Message.MessageType.ERROR, "syncmatica.error.share_modified_subregions");
            return;
        }
        button.setEnabled(false);
        final Context con = LitematicManager.getInstance().getActiveContext();
        final ExchangeTarget server = ((ClientCommunicationManager) con.getCommunicationManager()).getServer();
        final ShareLitematicExchange ex = new ShareLitematicExchange(schem, server, con);
        con.getCommunicationManager().startExchange(ex);
    }

}
