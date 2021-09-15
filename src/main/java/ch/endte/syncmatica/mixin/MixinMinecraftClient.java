package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "disconnect()V", at = @At("HEAD"))
    private void shutdownSyncmatica(final CallbackInfo ci) {
        ScreenHelper.close();
        Syncmatica.shutdown();
        LitematicManager.clear();
    }
}
