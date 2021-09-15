package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.ServerPlacement;
import ch.endte.syncmatica.communication.exchange.DownloadExchange;
import ch.endte.syncmatica.communication.exchange.Exchange;
import ch.endte.syncmatica.communication.exchange.VersionHandshakeClient;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import fi.dy.masa.malilib.gui.Message;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class ClientCommunicationManager extends CommunicationManager {

    private final ExchangeTarget server;
    private final Collection<ServerPlacement> sharing;

    public ClientCommunicationManager(final ExchangeTarget server) {
        super();
        this.server = server;
        broadcastTargets.add(server);
        sharing = new HashSet<>();
    }

    public ExchangeTarget getServer() {
        return server;
    }

    @Override
    protected void handle(final ExchangeTarget source, final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.REGISTER_METADATA.identifier)) {
            final ServerPlacement placement = receiveMetaData(packetBuf);
            context.getSyncmaticManager().addPlacement(placement);
            return;
        }
        if (id.equals(PacketType.REMOVE_SYNCMATIC.identifier)) {
            final UUID placementId = packetBuf.readUuid();
            final ServerPlacement placement = context.getSyncmaticManager().getPlacement(placementId);
            if (placement != null) {
                final Exchange modifier = getModifier(placement);
                if (modifier != null) {
                    modifier.close(false);
                    notifyClose(modifier);
                }
                context.getSyncmaticManager().removePlacement(placement);
                if (LitematicManager.getInstance().isRendered(placement)) {
                    LitematicManager.getInstance().unrenderSyncmatic(placement);
                }
            }
            return;
        }
        if (id.equals(PacketType.MODIFY.identifier)) {
            final UUID placementId = packetBuf.readUuid();
            final ServerPlacement toModify = context.getSyncmaticManager().getPlacement(placementId);
            receivePositionData(toModify, packetBuf);
            LitematicManager.getInstance().updateRendered(toModify);
            context.getSyncmaticManager().updateServerPlacement(toModify);
            return;
        }
        if (id.equals(PacketType.MESSAGE.identifier)) {
            final Message.MessageType type = mapMessageType(MessageType.valueOf(packetBuf.readString(32767)));
            final String text = packetBuf.readString(32767);
            ScreenHelper.ifPresent(s -> s.addMessage(type, text));
            return;
        }
    }

    @Override
    protected void handleExchange(final Exchange exchange) {
        if (exchange instanceof DownloadExchange && exchange.isSuccessful()) {
            LitematicManager.getInstance().renderSyncmatic(((DownloadExchange) exchange).getPlacement());
        }
    }

    @Override
    public void setDownloadState(final ServerPlacement syncmatic, final boolean state) {
        downloadState.put(syncmatic.getHash(), state);
        if (state || LitematicManager.getInstance().isRendered(syncmatic)) { //change client behavior so that the Load button doesn't show up naturally
            context.getSyncmaticManager().updateServerPlacement(syncmatic);
        }
    }

    public void setSharingState(final ServerPlacement placement, final boolean state) {
        if (state) {
            sharing.add(placement);
        } else {
            sharing.remove(placement);
        }
    }

    public boolean getSharingState(final ServerPlacement placement) {
        return sharing.contains(placement);
    }

    @Override
    public void setContext(final Context con) {
        super.setContext(con);
        final VersionHandshakeClient hi = new VersionHandshakeClient(server, context);
        startExchangeUnchecked(hi);
    }

    private Message.MessageType mapMessageType(final MessageType m) {
        switch (m) {
            case SUCCESS:
                return Message.MessageType.SUCCESS;
            case WARNING:
                return Message.MessageType.WARNING;
            case ERROR:
                return Message.MessageType.ERROR;
            default:
                return Message.MessageType.INFO;
        }
    }
}
