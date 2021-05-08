package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

@Mixin(ButtonBase.class)
public interface MixinButtonBase {
	@Accessor(value="actionListener", remap=false)
	IButtonActionListener getActionListener();
}
