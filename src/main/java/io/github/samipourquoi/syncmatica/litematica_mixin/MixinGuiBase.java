package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.gui.GuiBase;
import io.github.samipourquoi.syncmatica.litematica.ScreenUpdater;

@Mixin(GuiBase.class)
public class MixinGuiBase {

	public MixinGuiBase() {
	}
	
	@Inject(method = "removed()V", at = @At("TAIL"))
	public void removeScreenUpdater(CallbackInfo ci) {
		ScreenUpdater.getInstance().setCurrentWidget(null);
	}

}
