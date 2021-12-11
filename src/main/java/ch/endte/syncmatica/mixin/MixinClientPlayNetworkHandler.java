package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.mixin_actor.ActorClientPlayNetworkHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void startupEvent(final MinecraftClient client, final Screen screen, final ClientConnection connection, final GameProfile profile, final TelemetrySender telemetrySender, final CallbackInfo ci) {
        ActorClientPlayNetworkHandler.getInstance().startEvent((ClientPlayNetworkHandler) (Object) this);
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handlePacket(final CustomPayloadS2CPacket packet, final CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            return; //only execute packet on main thread
        }
        ActorClientPlayNetworkHandler.getInstance().packetEvent(packet, ci);
    }
}
