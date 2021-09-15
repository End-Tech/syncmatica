package ch.endte.syncmatica.litematica_mixin;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ButtonBase.class)
public interface MixinButtonBase {
    @Accessor(value = "actionListener", remap = false)
    IButtonActionListener getActionListener();
}
