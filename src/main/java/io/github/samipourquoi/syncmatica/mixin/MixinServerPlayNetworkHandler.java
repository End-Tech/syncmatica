package io.github.samipourquoi.syncmatica.mixin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ServerCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import net.minecraft.network.ClientConnection;
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

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConnect(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		LogManager.getLogger(ServerPlayNetworkHandler.class).info("server player join");
		((ServerCommunicationManager)Syncmatica.getCommunicationManager()).onPlayerJoin(getExchangeTarget());
	}
	
	@Inject(method = "onDisconnected", at = @At("HEAD"))
	public void onDisconnected(Text reason, CallbackInfo ci) {
		LogManager.getLogger(ServerPlayNetworkHandler.class).info("server player leave");
		((ServerCommunicationManager)Syncmatica.getCommunicationManager()).onPlayerLeave(getExchangeTarget());
	}
	
	@Inject(method = "onCustomPayload", at = @At("HEAD"))
	public void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		Identifier id = ((MixinCustomPayloadC2SPacket)packet).getChannel();
		LogManager.getLogger(ServerPlayNetworkHandler.class).info("received server"+id.toString());
		PacketByteBuf packetBuf = ((MixinCustomPayloadC2SPacket)packet).getData();
		Syncmatica.getCommunicationManager().onPacket(getExchangeTarget(), id, packetBuf);
	}
	
	private ExchangeTarget getExchangeTarget() {
		if (exTarget == null) {
			exTarget = new ExchangeTarget((ServerPlayNetworkHandler)(Object)this);
		}
		return exTarget;
	}
}
