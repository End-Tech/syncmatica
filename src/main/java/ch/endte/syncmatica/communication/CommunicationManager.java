package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.ServerPlacement;
import ch.endte.syncmatica.communication.exchange.DownloadExchange;
import ch.endte.syncmatica.communication.exchange.Exchange;
import ch.endte.syncmatica.util.SyncmaticaUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public abstract class CommunicationManager {
    protected final Collection<ExchangeTarget> broadcastTargets;

    // TODO: Refactor this bs
    protected final Map<UUID, Boolean> downloadState;
    protected final Map<UUID, Exchange> modifyState;

    protected Context context;

    protected static final BlockRotation[] rotOrdinals = BlockRotation.values();
    protected static final BlockMirror[] mirOrdinals = BlockMirror.values();

    protected CommunicationManager() {
        broadcastTargets = new ArrayList<>();
        downloadState = new HashMap<>();
        modifyState = new HashMap<>();
    }

    public boolean handlePacket(final Identifier id) {
        return PacketType.containsIdentifier(id);
    }

    public void onPacket(final ExchangeTarget source, final Identifier id, final PacketByteBuf packetBuf) {
        context.getDebugService().logReceivePacket(id);
        Exchange handler = null;
        final Collection<Exchange> potentialMessageTarget = source.getExchanges();
        if (potentialMessageTarget != null) {
            for (final Exchange target : potentialMessageTarget) {
                if (target.checkPacket(id, packetBuf)) {
                    target.handle(id, packetBuf);
                    handler = target;
                    break;
                }
            }
        }
        if (handler == null) {
            handle(source, id, packetBuf);
        } else if (handler.isFinished()) {
            notifyClose(handler);
        }
    }

    // will get called for every packet not handled by an exchange
    protected abstract void handle(ExchangeTarget source, Identifier id, PacketByteBuf packetBuf);

    // will get called for every finished exchange (successful or not)
    protected abstract void handleExchange(Exchange exchange);

    public void sendMetaData(final ServerPlacement metaData, final ExchangeTarget target) {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        putMetaData(metaData, buf);
        target.sendPacket(PacketType.REGISTER_METADATA.identifier, buf, context);
    }

    public void putMetaData(final ServerPlacement metaData, final PacketByteBuf buf) {
        buf.writeUuid(metaData.getId());

        buf.writeString(SyncmaticaUtil.sanitizeFileName(metaData.getName()));
        buf.writeUuid(metaData.getHash());

        putPositionData(metaData, buf);
    }

    public void putPositionData(final ServerPlacement metaData, final PacketByteBuf buf) {
        buf.writeBlockPos(metaData.getPosition());
        buf.writeString(metaData.getDimension());
        // one of the rare use cases for ordinal
        // transmitting the information of a non modifying enum to another
        // instance of this application with no regard to the persistence
        // of the ordinal values over time
        buf.writeInt(metaData.getRotation().ordinal());
        buf.writeInt(metaData.getMirror().ordinal());
    }

    public ServerPlacement receiveMetaData(final PacketByteBuf buf) {
        final UUID id = buf.readUuid();

        final String fileName = SyncmaticaUtil.sanitizeFileName(buf.readString(32767));
        final UUID hash = buf.readUuid();
        final ServerPlacement placement = new ServerPlacement(id, fileName, hash);

        receivePositionData(placement, buf);

        return placement;
    }

    public void receivePositionData(final ServerPlacement placement, final PacketByteBuf buf) {
        final BlockPos pos = buf.readBlockPos();
        final String dimensionId = buf.readString(32767);
        final BlockRotation rot = rotOrdinals[buf.readInt()];
        final BlockMirror mir = mirOrdinals[buf.readInt()];
        placement.move(dimensionId, pos, rot, mir);
    }

    public void download(final ServerPlacement syncmatic, final ExchangeTarget source) throws NoSuchAlgorithmException, IOException {
        if (!context.getFileStorage().getLocalState(syncmatic).isReadyForDownload()) {
            // forgot a negation here
            throw new IllegalArgumentException(syncmatic.toString() + " is not ready for download local state is: " + context.getFileStorage().getLocalState(syncmatic).toString());
        }
        final File toDownload = context.getFileStorage().createLocalLitematic(syncmatic);
        final Exchange downloadExchange = new DownloadExchange(syncmatic, toDownload, source, context);
        setDownloadState(syncmatic, true);
        startExchange(downloadExchange);
        context.getSyncmaticManager().updateServerPlacement(syncmatic);
    }

    public void setDownloadState(final ServerPlacement syncmatic, final boolean b) {
        downloadState.put(syncmatic.getHash(), b);
        context.getSyncmaticManager().updateServerPlacement(syncmatic);
    }

    public boolean getDownloadState(final ServerPlacement syncmatic) {
        return downloadState.getOrDefault(syncmatic.getHash(), false);
    }

    public void setModifier(final ServerPlacement syncmatic, final Exchange exchange) {
        modifyState.put(syncmatic.getHash(), exchange);
    }

    public Exchange getModifier(final ServerPlacement syncmatic) {
        return modifyState.get(syncmatic.getHash());
    }

    public void startExchange(final Exchange newExchange) {
        if (!broadcastTargets.contains(newExchange.getPartner())) {
            throw new IllegalArgumentException(newExchange.getPartner().toString() + " is not a valid ExchangeTarget");
        }
        startExchangeUnchecked(newExchange);
    }

    protected void startExchangeUnchecked(final Exchange newExchange) {
        newExchange.getPartner().getExchanges().add(newExchange);
        newExchange.init();
        if (newExchange.isFinished()) {
            notifyClose(newExchange);
        }
    }

    public void setContext(final Context con) {
        if (context == null) {
            context = con;
        } else {
            throw new Context.DuplicateContextAssignmentException("Duplicate Context Assignment");
        }
    }

    public void notifyClose(final Exchange e) {
        e.getPartner().getExchanges().remove(e);
        handleExchange(e);
    }


}
