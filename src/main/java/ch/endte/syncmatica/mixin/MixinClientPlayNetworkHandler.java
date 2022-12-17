package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.mixin_actor.ActorClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handlePacket(final CustomPayloadS2CPacket packet, final CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            return; //only execute packet on main thread
        }
        ActorClientPlayNetworkHandler.getInstance().packetEvent((ClientPlayNetworkHandler) (Object) this, packet, ci);
    }
}
