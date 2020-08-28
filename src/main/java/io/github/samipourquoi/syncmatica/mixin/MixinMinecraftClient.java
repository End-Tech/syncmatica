package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.Syncmatica;
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
		Syncmatica.initClient();
	}
}
