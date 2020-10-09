package io.github.samipourquoi.syncmatica.mixin;

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
public abstract class MixinServerPlayNetworkHandler extends ServerPlayNetworkHandler {
	
	@Unique
	private ExchangeTarget exTarget = null;

	public MixinServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection,
			ServerPlayerEntity player) {
		super(server, connection, player);
	}
	
	@Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("TAIL"))
	public void onConnect(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		((ServerCommunicationManager)Syncmatica.getCommunicationManager()).onPlayerJoin(getExchangeTarget());
	}
	
	@Inject(method = "onDisconnected", at = @At("HEAD"))
	public void onDisconnected(Text reason, CallbackInfo ci) {
		((ServerCommunicationManager)Syncmatica.getCommunicationManager()).onPlayerLeave(getExchangeTarget());
	}
	
	@Inject(method = "onCustomPayload", at = @At("HEAD"))
	public void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		Identifier id = ((MixinCustomPayloadC2SPacket)packet).getChannel();
		PacketByteBuf packetBuf = ((MixinCustomPayloadC2SPacket)packet).getData();
		Syncmatica.getCommunicationManager().onPacket(getExchangeTarget(), id, packetBuf);
	}
	
	private ExchangeTarget getExchangeTarget() {
		if (exTarget == null) {
			exTarget = new ExchangeTarget(this);
		}
		return exTarget;
	}
}
