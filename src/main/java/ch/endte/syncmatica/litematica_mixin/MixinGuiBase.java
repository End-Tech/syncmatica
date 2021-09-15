package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.litematica.ScreenHelper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiBase.class)
public abstract class MixinGuiBase {

    @Shadow(remap = false)
    private List<ButtonBase> buttons;

    public List<ButtonBase> getButtons() {
        return buttons;
    }

    @Inject(method = "removed()V", at = @At("TAIL"))
    public void removeScreenUpdater(final CallbackInfo ci) {
        ScreenHelper.ifPresent((s) -> s.setCurrentGui(null));
    }

}
