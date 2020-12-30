package io.github.samipourquoi.syncmatica.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.ServerCommunicationManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
	
	@Unique
	private ExchangeTarget exTarget = null;
	@Unique
	private ServerCommunicationManager comManager = null;
	
	@Shadow
	private ServerPlayerEntity player;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConnect(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		getCommunicationManager().onPlayerJoin(getExchangeTarget());
	}
	
	@Inject(method = "onDisconnected", at = @At("HEAD"))
	public void onDisconnected(Text reason, CallbackInfo ci) {
		getCommunicationManager().onPlayerLeave(getExchangeTarget());
	}
	
	@Inject(method = "onCustomPayload", at = @At("HEAD"))
	public void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler)(Object)this, (ServerWorld)this.player.getServerWorld());
		Identifier id = ((MixinCustomPayloadC2SPacket)packet).getChannel();
		PacketByteBuf packetBuf = ((MixinCustomPayloadC2SPacket)packet).getData();
		getCommunicationManager().onPacket(getExchangeTarget(), id, packetBuf);
	}
	
	private ExchangeTarget getExchangeTarget() {
		if (exTarget == null) {
			exTarget = new ExchangeTarget((ServerPlayNetworkHandler)(Object)this);
		}
		return exTarget;
	}
	
	private ServerCommunicationManager getCommunicationManager() {
		if (comManager == null) {
			Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
			if (con != null) {
				comManager = (ServerCommunicationManager)con.getCommunicationManager();
			}
		}
		return comManager;
	}
}
