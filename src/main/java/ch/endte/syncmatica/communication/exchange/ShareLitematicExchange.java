package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.RedirectFileStorage;
import ch.endte.syncmatica.ServerPlacement;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.PacketType;
import ch.endte.syncmatica.litematica.LitematicManager;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;

public class ShareLitematicExchange extends AbstractExchange {

    private final SchematicPlacement schem;
    private final ServerPlacement toShare;
    private final File toUpload;

    public ShareLitematicExchange(final SchematicPlacement schem, final ExchangeTarget partner, final Context con) {
        this(schem, partner, con, null);
    }

    public ShareLitematicExchange(final SchematicPlacement schem, final ExchangeTarget partner, final Context con, final ServerPlacement p) {
        super(partner, con);
        this.schem = schem;
        toShare = p == null ? LitematicManager.getInstance().syncmaticFromSchematic(schem) : p;
        toUpload = schem.getSchematicFile();
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.REQUEST_LITEMATIC.IDENTIFIER)
                || id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)
                || id.equals(PacketType.CANCEL_SHARE.IDENTIFIER)) {
            return AbstractExchange.checkUUID(packetBuf, toShare.getId());
        }
        return false;
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.REQUEST_LITEMATIC.IDENTIFIER)) {
            packetBuf.readUuid();
            UploadExchange upload = null;
            try {
                upload = new UploadExchange(toShare, toUpload, getPartner(), getContext());
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
            if (upload == null) {
                return;
            }
            getManager().startExchange(upload);
            return;
        }
        if (id.equals(PacketType.REGISTER_METADATA.IDENTIFIER)) {
            final RedirectFileStorage redirect = (RedirectFileStorage) getContext().getFileStorage();
            redirect.addRedirect(toUpload);
            LitematicManager.getInstance().renderSyncmatic(toShare, schem, false);
            getContext().getSyncmaticManager().addPlacement(toShare);
            return;
        }
        if (id.equals(PacketType.CANCEL_SHARE.IDENTIFIER)) {
            close(false);
        }
    }

    @Override
    public void init() {
        if (toShare == null) {
            close(false);
            return;
        }
        ((ClientCommunicationManager) getManager()).setSharingState(toShare, true);
        getContext().getSyncmaticManager().updateServerPlacement(toShare);
        getManager().sendMetaData(toShare, getPartner());
    }

    @Override
    public void onClose() {
        ((ClientCommunicationManager) getManager()).setSharingState(toShare, false);
    }

    @Override
    protected void sendCancelPacket() {
    }
}
