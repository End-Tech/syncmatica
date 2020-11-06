package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.util.JsonUtils;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.github.samipourquoi.syncmatica.litematica.gui.IIDContainer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

@Mixin(SchematicPlacement.class)
public abstract class MixinSchematicPlacement implements IIDContainer {

	// unsure if I can just make an assignment here so I mixin to the constructor
	@Unique
	UUID serverId;
	
	public MixinSchematicPlacement() {
	}
	
	@Inject(method="toJson", at = @At("RETURN"), remap=false)
	public void saveUuid(CallbackInfoReturnable<JsonObject> cir) {
		JsonObject saveData = cir.getReturnValue();
		LogManager.getLogger(ClientPlayNetworkHandler.class).info("Called toJson");
		if (saveData != null) {
			LogManager.getLogger(ClientPlayNetworkHandler.class).info("saveData does not equal null");
			if (serverId != null) {
				LogManager.getLogger(ClientPlayNetworkHandler.class).info("is Syncmatic");
				saveData.add("syncmatica_uuid", new JsonPrimitive(serverId.toString()));
			}
		}
	}
	
	@Inject(method = "fromJson", at = @At("RETURN"), remap = false, cancellable = true)
	private static void loadSyncmatic(JsonObject obj, CallbackInfoReturnable<SchematicPlacement> cir) {
		if (JsonUtils.hasString(obj, "syncmatica_uuid")) {
			SchematicPlacement newInstance = cir.getReturnValue();
			((IIDContainer)newInstance).setServerId(UUID.fromString(obj.get("syncmatica_uuid").getAsString()));
			cir.setReturnValue(null);
			LitematicManager.getInstance().preLoad(newInstance);
		}
	}
	
	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	public void setNull(LitematicaSchematic schematic, BlockPos origin, String name, boolean enabled, boolean enableRender, CallbackInfo ci) {
		serverId = null;
	}
	
	@Override
	public void setServerId(UUID i) {
		serverId = i;
	}
	
	@Override
	public UUID getServerId() {
		return serverId;
	}
	
	

}
