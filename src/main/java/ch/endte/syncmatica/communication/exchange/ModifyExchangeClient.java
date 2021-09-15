package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Feature;
import ch.endte.syncmatica.ServerPlacement;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.PacketType;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.Message;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ModifyExchangeClient extends AbstractExchange {

    //bad practice but valid for communiction with deprecated systems
    private boolean expectRemove = false;

    private final ServerPlacement placement;
    private final SchematicPlacement litematic;

    private ShareLitematicExchange legacyModify;

    public ModifyExchangeClient(final ServerPlacement placement, final ExchangeTarget partner, final Context con) {
        super(partner, con);
        this.placement = placement;
        litematic = LitematicManager.getInstance().schematicFromSyncmatic(placement);
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.MODIFY_REQUEST_DENY.IDENTIFIER)
                || id.equals(PacketType.MODIFY_REQUEST_ACCEPT.IDENTIFIER)
                || (expectRemove && id.equals(PacketType.REMOVE_SYNCMATIC.IDENTIFIER))) {
            return checkUUID(packetBuf, placement.getId());
        }
        return false;
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.MODIFY_REQUEST_DENY.IDENTIFIER)) {
            packetBuf.readUuid();
            close(false);
            if (!litematic.isLocked()) {
                litematic.setOrigin(placement.getPosition(), null);
                litematic.setRotation(placement.getRotation(), null);
                litematic.setMirror(placement.getMirror(), null);
                litematic.toggleLocked();
            }
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.SUCCESS, "syncmatica.error.modification_deny"));
        } else if (id.equals(PacketType.MODIFY_REQUEST_ACCEPT.IDENTIFIER)) {
            packetBuf.readUuid();
            acceptModification();
        } else if (id.equals(PacketType.REMOVE_SYNCMATIC.IDENTIFIER)) {
            packetBuf.readUuid();
            legacyModify = new ShareLitematicExchange(litematic, getPartner(), getContext(), placement);
            getContext().getCommunicationManager().startExchange(legacyModify);
            succeed(); // the adding portion of this is handled by the ShareLitematicExchange
        }
    }

    @Override
    public void init() {
        if (getContext().getCommunicationManager().getModifier(placement) != null) {
            close(false);
            return;
        }
        getContext().getCommunicationManager().setModifier(placement, this);
        if (getPartner().getFeatureSet().hasFeature(Feature.MODIFY)) {
            final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(placement.getId());
            getPartner().sendPacket(PacketType.MODIFY_REQUEST.IDENTIFIER, buf, getContext());
        } else {
            acceptModification();
        }
    }

    private void acceptModification() {
        if (litematic.isLocked()) {
            litematic.toggleLocked();
        }
        ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.SUCCESS, "syncmatica.success.modification_accepted"));
        getContext().getSyncmaticManager().updateServerPlacement(placement);
    }

    public void conclude() {
        final String dimension = MinecraftClient.getInstance().getCameraEntity().getEntityWorld().getRegistryKey().getValue().toString();
        placement.move(dimension, litematic.getOrigin(), litematic.getRotation(), litematic.getMirror());
        sendFinish();
        if (!litematic.isLocked()) {
            litematic.toggleLocked();
        }
        getContext().getSyncmaticManager().updateServerPlacement(placement);
    }

    private void sendFinish() {
        if (getPartner().getFeatureSet().hasFeature(Feature.MODIFY)) {
            final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(placement.getId());
            getContext().getCommunicationManager().putPositionData(placement, buf);
            getPartner().sendPacket(PacketType.MODIFY_FINISH.IDENTIFIER, buf, getContext());
            succeed();
            getContext().getCommunicationManager().notifyClose(this);
        } else {
            final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(placement.getId());
            getPartner().sendPacket(PacketType.REMOVE_SYNCMATIC.IDENTIFIER, buf, getContext());
            expectRemove = true;
        }
    }

    @Override
    protected void sendCancelPacket() {
        if (getPartner().getFeatureSet().hasFeature(Feature.MODIFY)) {
            final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(placement.getId());
            getContext().getCommunicationManager().putPositionData(placement, buf);
            getPartner().sendPacket(PacketType.MODIFY_FINISH.IDENTIFIER, buf, getContext());
        }
    }

    @Override
    protected void onClose() {
        if (getContext().getCommunicationManager().getModifier(placement) == this) {
            getContext().getCommunicationManager().setModifier(placement, null);
        }
    }

}
