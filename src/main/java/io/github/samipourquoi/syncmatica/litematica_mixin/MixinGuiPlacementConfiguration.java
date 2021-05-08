package io.github.samipourquoi.syncmatica.litematica_mixin;

import fi.dy.masa.litematica.gui.GuiPlacementConfiguration;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.exchange.ModifyExchangeClient;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.github.samipourquoi.syncmatica.litematica.ScreenHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiPlacementConfiguration.class)
public abstract class MixinGuiPlacementConfiguration extends GuiBase {

    @Final
    @Shadow(remap = false)
    public SchematicPlacement placement;

    @Inject(method = "initGui", at = @At("RETURN"), remap = false)
    public void initGui(final CallbackInfo ci) {
        if (!LitematicManager.getInstance().isSyncmatic(placement)) {
            return;
        }
        final List<ButtonBase> buttons = ((MixinGuiBase) (Object) this).getButtons();
        final ButtonBase button = buttons.get(6); // unlock button
        button.setActionListener((b, k) -> {
            if (placement.isLocked()) {
                requestModification();
            } else {
                if (placement.isRegionPlacementModified()) {
                    addMessage(Message.MessageType.ERROR, "syncmatica.error.share_modified_subregions");
                    return;
                }
                finishModification();
            }
        });
        ScreenHelper.ifPresent(s -> s.setCurrentGui(this));
    }

    private void requestModification() {
        final Context context = LitematicManager.getInstance().getActiveContext();
        final ExchangeTarget server = ((ClientCommunicationManager) context.getCommunicationManager()).getServer();
        final ServerPlacement serverPlacement = LitematicManager.getInstance().syncmaticFromSchematic(placement);

        final ModifyExchangeClient modifyExchange = new ModifyExchangeClient(serverPlacement, server, context);
        context.getCommunicationManager().startExchange(modifyExchange);
    }

    private void finishModification() {
        final Context context = LitematicManager.getInstance().getActiveContext();
        final ServerPlacement serverPlacement = LitematicManager.getInstance().syncmaticFromSchematic(placement);
        final ModifyExchangeClient modifyExchange = (ModifyExchangeClient) context.getCommunicationManager().getModifier(serverPlacement);
        if (modifyExchange != null) {
            modifyExchange.conclude();
        }
    }
}
