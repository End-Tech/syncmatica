package io.github.samipourquoi.syncmatica.communication.exchange;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.FeatureSet;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FeatureExchange extends AbstractExchange {

    boolean request = false;

    public FeatureExchange(final ExchangeTarget partner, final Context con) {
        super(partner, con);
    }

    public FeatureExchange(final ExchangeTarget partner, final Context con, final boolean request) {
        this(partner, con);
        this.request = request;
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        return id.equals(PacketType.FEATURE_REQUEST.IDENTIFIER)
                || id.equals(PacketType.FEATURE.IDENTIFIER);
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.FEATURE_REQUEST.IDENTIFIER)) {
            sendFeatures();
        } else if (id.equals(PacketType.FEATURE.IDENTIFIER)) {
            final FeatureSet fs = FeatureSet.fromString(packetBuf.readString(32767));
            getPartner().setFeatureSet(fs);
            onFeatureSetReceive();
        }
    }

    protected void onFeatureSetReceive() {
        succeed();
    }

    ;

    public void requestFeatureSet() {
        getPartner().sendPacket(PacketType.FEATURE_REQUEST.IDENTIFIER, new PacketByteBuf(Unpooled.buffer()), getContext());
    }

    private void sendFeatures() {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        final FeatureSet fs = getContext().getFeatureSet();
        buf.writeString(fs.toString(), 32767);
        getPartner().sendPacket(PacketType.FEATURE.IDENTIFIER, buf, getContext());
    }

    @Override
    public void init() {
        if (request) {
            requestFeatureSet();
        } else {
            sendFeatures(); // does that really need encapsulation in this class?
            succeed(); // even had to add special handling to this case which might never occur
        }
    }

    @Override
    protected void sendCancelPacket() {
    }

}
