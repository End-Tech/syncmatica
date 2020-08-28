package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.SchematicManager;
import io.github.samipourquoi.syncmatica.packets.PacketType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Inject(method = "onCustomPayload", at = @At(
			value = "INVOKE",
			target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"
	))
	private void handleRegisterLitematic(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		Identifier identifier = packet.getChannel();
		PacketByteBuf buf = packet.getData();

		if (PacketType.REGISTER.IDENTIFIER.equals(identifier)) {
			SchematicManager.INSTANCE.createPlacement(buf);
		}
	}
}
