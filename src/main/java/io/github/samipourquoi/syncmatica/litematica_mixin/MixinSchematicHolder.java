package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.litematica.data.SchematicHolder;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;

@Mixin(SchematicHolder.class)
public abstract class MixinSchematicHolder {

	public MixinSchematicHolder() {}
	
	@Inject(method="removeSchematic", at = @At("RETURN"), remap = false)
	public void unloadSyncmatic(LitematicaSchematic schematic, CallbackInfoReturnable<Boolean> ci) {
		LitematicManager.getInstance().unrenderSchematic(schematic);
	}

}
