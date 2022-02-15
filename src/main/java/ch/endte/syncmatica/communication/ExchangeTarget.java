package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.exchange.Exchange;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// since Client/Server PlayNetworkHandler are 2 different classes, but I want to use exchanges
// on both without having to recode them individually I have an adapter class here

public class ExchangeTarget {
    private ClientPlayNetworkHandler server = null;
    private ServerPlayNetworkHandler client = null;
    private final String persistentName;

    private FeatureSet features;
    private final List<Exchange> ongoingExchanges = new ArrayList<>(); // implicitly relies on priority

    public ExchangeTarget(final ClientPlayNetworkHandler server) {
        this.server = server;
        persistentName = StringUtils.getWorldOrServerName();
    }

    public ExchangeTarget(final ServerPlayNetworkHandler client) {
        this.client = client;
        persistentName = client.player.getUuidAsString();
    }

    // this application exclusively communicates in CustomPayLoad packets
    // this class handles the sending of either S2C or C2S packets
    public void sendPacket(final Identifier id, final PacketByteBuf packetBuf, final Context context) {
        context.getDebugService().logSendPacket(id, persistentName);
        if (server == null) {
            final CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(id, packetBuf);
            client.sendPacket(packet);
        } else {
            final CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(id, packetBuf);
            server.sendPacket(packet);
        }
    }

    // removed equals code due to issues with Collection.contains

    public FeatureSet getFeatureSet() {
        return features;
    }

    public void setFeatureSet(final FeatureSet f) {
        features = f;
    }

    public Collection<Exchange> getExchanges() {
        return ongoingExchanges;
    }

    public String getPersistentName() {
        return persistentName;
    }
}
