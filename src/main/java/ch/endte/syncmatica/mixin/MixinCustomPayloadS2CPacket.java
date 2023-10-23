package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.SyncmaticaPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomPayloadS2CPacket.class)
public class MixinCustomPayloadS2CPacket {
    @Inject(method = "readPayload", at = @At(value = "HEAD"), cancellable = true)
    private static void onOnCustomPayload(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
        if (id.getNamespace().equals(Syncmatica.MOD_ID)) {
            cir.setReturnValue(new SyncmaticaPayload(id, buf));
        }
    }
}
