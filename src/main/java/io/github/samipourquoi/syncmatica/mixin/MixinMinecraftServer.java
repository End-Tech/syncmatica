package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.Syncmatica;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(method = "startServer", at = @At("RETURN"))
	private static <S extends MinecraftServer> void initSyncmatica(Function<Thread, S> serverFactory, CallbackInfoReturnable<S> cir) {
		Syncmatica.initServer();
	}
}
