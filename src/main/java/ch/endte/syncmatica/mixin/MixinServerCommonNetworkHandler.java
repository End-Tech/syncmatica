package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.communication.PacketType;
import ch.endte.syncmatica.network.IServerPlayerNetworkHandler;
import ch.endte.syncmatica.network.SyncmaticaPayload;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public class MixinServerCommonNetworkHandler {
    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    private void onCustomCarpetPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        Object thiss = this;
        if (thiss instanceof ServerPlayNetworkHandler impl && packet.payload() instanceof SyncmaticaPayload payload) {
            // ChannelManager.onChannelRegisterHandle(playerNetworkHandler.ggetExchangeTarget(), packet.getChannel(), packet.getData());
            if (PacketType.containsIdentifier(payload.id())) {
                NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), payload.id(), payload.byteBuf()));
            }
        }
    }
}
