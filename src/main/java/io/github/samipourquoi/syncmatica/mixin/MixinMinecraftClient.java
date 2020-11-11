package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.github.samipourquoi.syncmatica.litematica.ScreenUpdater;
import net.minecraft.client.MinecraftClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Inject(method = "disconnect()V", at = @At("HEAD"))
	private void shutdownSyncmatica(CallbackInfo ci) {
		ScreenUpdater.close();
		Syncmatica.shutdown();
		LitematicManager.clear();
	}
}
