package io.github.samipourquoi.syncmatica.mixin;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import io.github.samipourquoi.syncmatica.SchematicManager;
import io.github.samipourquoi.syncmatica.Syncmatica;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void initSyncmatica(RunArgs args, CallbackInfo ci) {
		Syncmatica.initClient();
	}

	@Inject(method = "disconnect()V", at = @At("HEAD"))
	private void removePlacements(CallbackInfo ci) {
		SchematicPlacementManager manager = DataManager.getSchematicPlacementManager();

		// Removes all the syncmatics
		for (SchematicPlacement placement : SchematicManager.INSTANCE.schematics) {
			manager.removeSchematicPlacement(placement);
		}
	}
}
