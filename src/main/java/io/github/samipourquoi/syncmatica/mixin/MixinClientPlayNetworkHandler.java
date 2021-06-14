package io.github.samipourquoi.syncmatica.mixin;

import com.mojang.authlib.GameProfile;
import io.github.samipourquoi.syncmatica.IFileStorage;
import io.github.samipourquoi.syncmatica.RedirectFileStorage;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.github.samipourquoi.syncmatica.litematica.ScreenHelper;
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


@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Unique
    private ExchangeTarget exTarget = null;
    @Unique
    private CommunicationManager clientCommunication;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void startupClient(final MinecraftClient client, final Screen screen, final ClientConnection connection, final GameProfile profile, final CallbackInfo ci) {
        final IFileStorage data = new RedirectFileStorage();
        final SyncmaticManager man = new SyncmaticManager();
        exTarget = new ExchangeTarget((ClientPlayNetworkHandler) (Object) this);
        final CommunicationManager comms = new ClientCommunicationManager(exTarget);
        Syncmatica.initClient(comms, data, man);
        clientCommunication = comms;
        ScreenHelper.init();
        LitematicManager.getInstance().setActiveContext(Syncmatica.getContext(Syncmatica.CLIENT_CONTEXT));
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handlePacket(final CustomPayloadS2CPacket packet, final CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            return; //only execute packet on main thread
        }
        final Identifier id = packet.getChannel();
        final PacketByteBuf buf = packet.getData();
        if (clientCommunication.handlePacket(exTarget, id, buf)) {
            clientCommunication.onPacket(exTarget, id, buf);
            ci.cancel(); // prevent further unnecessary comparisons and reporting a warning
        }
    }
}
