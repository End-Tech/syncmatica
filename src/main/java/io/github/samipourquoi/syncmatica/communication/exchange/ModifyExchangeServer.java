package io.github.samipourquoi.syncmatica.communication.exchange;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ModifyExchangeServer extends AbstractExchange {

    private final ServerPlacement placement;
    UUID placementId;

    public ModifyExchangeServer(final UUID placeId, final ExchangeTarget partner, final Context con) {
        super(partner, con);
        placementId = placeId;
        placement = con.getSyncmaticManager().getPlacement(placementId);
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        return id.equals(PacketType.MODIFY_FINISH.IDENTIFIER) && AbstractExchange.checkUUID(packetBuf, placement.getId());
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        packetBuf.readUuid(); // consume uuid
        if (id.equals(PacketType.MODIFY_FINISH.IDENTIFIER)) {
            getContext().getCommunicationManager().receivePositionData(placement, packetBuf);
            succeed();
        }
    }

    @Override
    public void init() {
        if (getPlacement() == null || getContext().getCommunicationManager().getModifier(placement) != null) {
            close(true); // equivalent to deny
        } else {
            accept();
        }
    }

    private void accept() {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(placement.getId());
        getPartner().sendPacket(PacketType.MODIFY_REQUEST_ACCEPT.IDENTIFIER, buf, getContext());
        getContext().getCommunicationManager().setModifier(placement, this);
    }

    @Override
    protected void sendCancelPacket() {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(placementId);
        getPartner().sendPacket(PacketType.MODIFY_REQUEST_DENY.IDENTIFIER, buf, getContext());
    }

    public ServerPlacement getPlacement() {
        return placement;
    }

    @Override
    protected void onClose() {
        if (getContext().getCommunicationManager().getModifier(placement) == this) {
            getContext().getCommunicationManager().setModifier(placement, null);
        }
    }

}
