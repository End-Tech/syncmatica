package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.RedirectFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.Exchange.ExchangeTarget;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void initSyncmatica(RunArgs args, CallbackInfo ci) {
		
		IFileStorage data = new RedirectFileStorage();
		SyncmaticManager man = new SyncmaticManager();
		ExchangeTarget server = new ExchangeTarget(MinecraftClient.getInstance().getNetworkHandler());
		CommunicationManager comms = new ClientCommunicationManager(data, man, server);
		
		Syncmatica.initClient(comms, data, man);
	}

	@Inject(method = "disconnect()V", at = @At("HEAD"))
	private void shutdownSyncmatica(CallbackInfo ci) {
		Syncmatica.shutdown();
		LitematicManager.clear();
	}
}
