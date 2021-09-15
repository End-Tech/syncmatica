package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.*;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(method = "startServer", at = @At("TAIL"))
    private static <S extends MinecraftServer> void initSyncmatica(final Function<Thread, S> serverFactory, final CallbackInfoReturnable<S> ci) {
        final IFileStorage data = new FileStorage();
        final SyncmaticManager man = new SyncmaticManager();
        final CommunicationManager comms = new ServerCommunicationManager();

        Syncmatica.initServer(comms, data, man);
        final Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
        con.startup();
    }

    // at
    @Inject(method = "shutdown", at = @At("TAIL"))
    public void shutdownSyncmatica(final CallbackInfo ci) {
        final Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
        con.shutdown();
    }
}
