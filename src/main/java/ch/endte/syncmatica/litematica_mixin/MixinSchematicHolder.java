package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.litematica.LitematicManager;
import fi.dy.masa.litematica.data.SchematicHolder;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SchematicHolder.class)
public abstract class MixinSchematicHolder {

    public MixinSchematicHolder() {
    }

    @Inject(method = "removeSchematic", at = @At("RETURN"), remap = false)
    public void unloadSyncmatic(final LitematicaSchematic schematic, final CallbackInfoReturnable<Boolean> ci) {
        LitematicManager.getInstance().unrenderSchematic(schematic);
    }

}
