package ch.endte.syncmatica.mixin;

import java.util.function.Consumer;

import ch.endte.syncmatica.network.ChannelManager;
import ch.endte.syncmatica.network.IServerPlayerNetworkHandler;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


@Mixin(value = ServerPlayNetworkHandler.class, priority = 998)
public abstract class MixinServerPlayNetworkHandler implements IServerPlayerNetworkHandler {
    @Unique
    private ExchangeTarget exTarget = null;

    @Unique
    private ServerCommunicationManager comManager = null;

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    protected abstract ParseResults<ServerCommandSource> parse(String command);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onConnect(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        syncmatica$operateComms(sm -> sm.onPlayerJoin(syncmatica$getExchangeTarget(), player));
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(final Text reason, final CallbackInfo ci) {
        ChannelManager.onDisconnected();
        syncmatica$operateComms(sm -> sm.onPlayerLeave(syncmatica$getExchangeTarget()));
    }

    public void syncmatica$operateComms(final Consumer<ServerCommunicationManager> operation) {
        if (comManager == null) {
            final Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (con != null) {
                comManager = (ServerCommunicationManager) con.getCommunicationManager();
            }
        }
        if (comManager != null) {
            operation.accept(comManager);
        }
    }

    public ExchangeTarget syncmatica$getExchangeTarget() {
        if (exTarget == null) {
            exTarget = new ExchangeTarget((ServerPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
}
