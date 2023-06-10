package ch.endte.syncmatica.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {

    @Unique
    private ExchangeTarget exTarget = null;
    @Unique
    private ServerCommunicationManager comManager = null;

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onConnect(final MinecraftServer server, final ClientConnection connection, final ServerPlayerEntity player, final CallbackInfo ci) {
        operateComms(sm -> sm.onPlayerJoin(getExchangeTarget(), player));
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(final Text reason, final CallbackInfo ci) {
        operateComms(sm -> sm.onPlayerLeave(getExchangeTarget()));
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    public void onCustomPayload(final CustomPayloadC2SPacket packet, final CallbackInfo ci) {
        NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler) (Object) this, player.getServerWorld());
        final Identifier id = ((MixinCustomPayloadC2SPacket) packet).getChannel();
        final PacketByteBuf packetBuf = ((MixinCustomPayloadC2SPacket) packet).getData();
        operateComms(sm -> sm.onPacket(getExchangeTarget(), id, packetBuf));
    }

    private ExchangeTarget getExchangeTarget() {
        if (exTarget == null) {
            exTarget = new ExchangeTarget((ServerPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }

    private void operateComms(final Consumer<ServerCommunicationManager> operation) {
        if (comManager == null) {
            final Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (con != null) {
                comManager = (ServerCommunicationManager) con.getCommunicationManager();
            }
        }
        if (comManager != null) {
            operation.accept(comManager);
        }
    }
}
