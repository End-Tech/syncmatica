package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.RedirectFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.litematica.ScreenUpdater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;


@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {
	
	@Unique
	private ExchangeTarget exTarget = null;
	
	@Inject(method = "<init>", at= @At("TAIL"))
	private void startupClient(MinecraftClient client, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo ci) {
		IFileStorage data = new RedirectFileStorage();
		SyncmaticManager man = new SyncmaticManager();
		exTarget = new ExchangeTarget((ClientPlayNetworkHandler)(Object)this);
		CommunicationManager comms = new ClientCommunicationManager(data, man, exTarget);
		Syncmatica.initClient(comms, data, man);
		ScreenUpdater.init();
	}
	
	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void handlePacket(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (!MinecraftClient.getInstance().isOnThread()) {
			return; //only execute packet on main thread
		}
		Identifier id = packet.getChannel();
		PacketByteBuf buf = packet.getData();
		CommunicationManager comms = Syncmatica.getCommunicationManager();
		if (comms.handlePacket(exTarget, id, buf)) {
			comms.onPacket(exTarget, id, buf);
			ci.cancel(); // prevent further unnecessary comparisons and reporting a warning
		}
	}
}
