package io.github.samipourquoi.syncmatica.communication.exchange;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.MessageType;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.github.samipourquoi.syncmatica.communication.ServerCommunicationManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DownloadExchange extends AbstractExchange {

    private final ServerPlacement toDownload;
    private final OutputStream outputStream;
    private final MessageDigest md5;
    private final File downloadFile;
    private int bytesSent;

    public DownloadExchange(final ServerPlacement syncmatic, final File downloadFile, final ExchangeTarget partner, final Context context) throws IOException, NoSuchAlgorithmException {
        super(partner, context);
        this.downloadFile = downloadFile;
        final OutputStream os = new FileOutputStream(downloadFile); //NOSONAR
        toDownload = syncmatic;
        md5 = MessageDigest.getInstance("MD5");
        outputStream = new DigestOutputStream(os, md5);
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.SEND_LITEMATIC.IDENTIFIER)
                || id.equals(PacketType.FINISHED_LITEMATIC.IDENTIFIER)
                || id.equals(PacketType.CANCEL_LITEMATIC.IDENTIFIER)) {
            return checkUUID(packetBuf, toDownload.getId());
        }
        return false;
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        packetBuf.readUuid(); //skips the UUID
        if (id.equals(PacketType.SEND_LITEMATIC.IDENTIFIER)) {
            final int size = packetBuf.readInt();
            bytesSent += size;
            if (getContext().isServer()) {
                if (getContext().getQuotaService().isOverQuota(getPartner(), bytesSent)) {
                    close(true);
                    ((ServerCommunicationManager) getContext().getCommunicationManager()).sendMessage(
                            getPartner(),
                            MessageType.ERROR,
                            "syncmatica.error.cancelled_transmit_exceed_quota"
                    );
                }
            }
            try {
                packetBuf.readBytes(outputStream, size);
            } catch (final IOException e) {
                close(true);
                e.printStackTrace();
                return;
            }
            final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf.writeUuid(toDownload.getId());
            getPartner().sendPacket(PacketType.RECEIVED_LITEMATIC.IDENTIFIER, packetByteBuf);
            return;
        }
        if (id.equals(PacketType.FINISHED_LITEMATIC.IDENTIFIER)) {
            try {
                outputStream.flush();
            } catch (final IOException e) {
                close(false);
                e.printStackTrace();
                return;
            }
            final UUID downloadHash = UUID.nameUUIDFromBytes(md5.digest());
            if (downloadHash.equals(toDownload.getHash())) {
                succeed();
            } else {
                // no need to notify partner since exchange is closed on partner side
                close(false);
            }
            return;
        }
        if (id.equals(PacketType.CANCEL_LITEMATIC.IDENTIFIER)) {
            close(false);
        }
    }

    @Override
    public void init() {
        final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeUuid(toDownload.getId());
        getPartner().sendPacket(PacketType.REQUEST_LITEMATIC.IDENTIFIER, packetByteBuf);
    }

    @Override
    protected void onClose() {
        getManager().setDownloadState(toDownload, false);
        if (getContext().isServer() && isSuccessful()) {
            getContext().getQuotaService().progressQuota(getPartner(), bytesSent);
        }
        try {
            outputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        if (!isSuccessful()) {
            if (downloadFile.exists()) {
                downloadFile.delete(); //..but I dont want to do anything with it?
            }
        }
    }

    @Override
    protected void sendCancelPacket() {
        final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeUuid(toDownload.getId());
        getPartner().sendPacket(PacketType.CANCEL_LITEMATIC.IDENTIFIER, packetByteBuf);
    }

    public ServerPlacement getPlacement() {
        return toDownload;
    }

}
