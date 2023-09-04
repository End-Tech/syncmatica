package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.communication.PacketType;
import ch.endte.syncmatica.mixin_actor.ActorClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

// Higher level compared to fabric API. fabric-api=999
@Mixin(value = ClientPlayNetworkHandler.class, priority = 998)
public abstract class MixinClientPlayNetworkHandler {
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handlePacket(final CustomPayloadS2CPacket packet, final CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread() && !packet.getChannel().equals(PacketType.MINECRAFT_REGISTER.identifier)) {
            return; //only execute packet on main thread
        }
        ActorClientPlayNetworkHandler.getInstance().packetEvent((ClientPlayNetworkHandler) (Object) this, packet, ci);
    }
}
