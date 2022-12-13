package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.FileStorage;
import ch.endte.syncmatica.SyncmaticManager;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(method = "startServer", at = @At("RETURN"))
    private static <S extends MinecraftServer> void initSyncmatica(final Function<Thread, S> serverFactory, final CallbackInfoReturnable<S> ci) {
        final MinecraftServer returnValue = ci.getReturnValue();
        Syncmatica.initServer(
                new ServerCommunicationManager(),
                new FileStorage(),
                new SyncmaticManager(),
                returnValue.isDedicated(),
                returnValue.getSavePath(WorldSavePath.ROOT).toFile()
        ).startup();
    }

    // at
    @Inject(method = "shutdown", at = @At("TAIL"))
    public void shutdownSyncmatica(final CallbackInfo ci) {
        Syncmatica.shutdown();
    }
}
