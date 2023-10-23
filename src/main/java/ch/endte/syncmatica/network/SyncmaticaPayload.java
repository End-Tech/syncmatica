package ch.endte.syncmatica.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncmaticaPayload(PacketByteBuf byteBuf, Identifier id) implements CustomPayload {

    public SyncmaticaPayload(Identifier identifier, PacketByteBuf input) {
        this(new PacketByteBuf(input.readBytes(input.readableBytes())), identifier);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBytes(byteBuf);
    }
}
