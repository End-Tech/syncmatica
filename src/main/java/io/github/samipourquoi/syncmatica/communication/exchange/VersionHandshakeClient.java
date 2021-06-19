package io.github.samipourquoi.syncmatica.communication.exchange;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.FeatureSet;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

public class VersionHandshakeClient extends FeatureExchange {

    private String partnerVersion;

    public VersionHandshakeClient(final ExchangeTarget partner, final Context con) {
        super(partner, con);
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        return id.equals(PacketType.CONFIRM_USER.IDENTIFIER)
                || id.equals(PacketType.REGISTER_VERSION.IDENTIFIER)
                || super.checkPacket(id, packetBuf);
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.REGISTER_VERSION.IDENTIFIER)) {
            final String partnerVersion = packetBuf.readString(32767);
            if (!getContext().checkPartnerVersion(partnerVersion)) {
                // any further packets are risky so no further packets should get send
                LogManager.getLogger(VersionHandshakeClient.class).info("Denying syncmatica join due to outdated server with local version {} and server version {}", Syncmatica.VERSION, partnerVersion);
                close(false);
            } else {
                this.partnerVersion = partnerVersion;
                final FeatureSet fs = FeatureSet.fromVersionString(partnerVersion);
                if (fs == null) {
                    requestFeatureSet();
                } else {
                    getPartner().setFeatureSet(fs);
                    onFeatureSetReceive();
                }
            }
        } else if (id.equals(PacketType.CONFIRM_USER.IDENTIFIER)) {
            final int placementCount = packetBuf.readInt();
            for (int i = 0; i < placementCount; i++) {
                final ServerPlacement p = getManager().receiveMetaData(packetBuf);
                getContext().getSyncmaticManager().addPlacement(p);
            }
            LogManager.getLogger(VersionHandshakeClient.class).info("Joining syncmatica server with local version {} and server version {}", Syncmatica.VERSION, partnerVersion);
            LitematicManager.getInstance().commitLoad();
            getContext().startup();
            succeed();
        } else {
            super.handle(id, packetBuf);
        }
    }

    @Override
    public void onFeatureSetReceive() {
        final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        newBuf.writeString(Syncmatica.VERSION);
        getPartner().sendPacket(PacketType.REGISTER_VERSION.IDENTIFIER, newBuf, getContext());
    }

    @Override
    public void init() {
    }

    @Override
    protected void sendCancelPacket() {
    }

}
