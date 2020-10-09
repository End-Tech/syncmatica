package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler extends ClientPlayNetworkHandler {
	
	public MixinClientPlayNetworkHandler(MinecraftClient client, Screen screen, ClientConnection connection,
			GameProfile profile) {
		super(client, screen, connection, profile);
	}

	private ExchangeTarget exTarget = null;

	@Inject(method = "onCustomPayload", at = @At(
			value = "INVOKE",
			target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V",
			remap = false
	))
	private void handlePacket(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		Identifier id = packet.getChannel();
		PacketByteBuf buf = packet.getData();
		if (exTarget == null) {
			exTarget = new ExchangeTarget(this);
		}
		Syncmatica.getCommunicationManager().onPacket(exTarget, id, buf);
	}
}
