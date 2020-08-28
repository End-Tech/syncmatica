package io.github.samipourquoi.syncmatica.mixin;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import io.github.samipourquoi.syncmatica.SchematicManager;
import io.github.samipourquoi.syncmatica.packets.PacketType;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Inject(method = "onCustomPayload", at = @At(
			value = "INVOKE",
			target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"
	))
	private void handleRegisterLitematic(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		Identifier identifier = packet.getChannel();
		PacketByteBuf buf = packet.getData();

		if (PacketType.REGISTER.IDENTIFIER.equals(identifier)) {
			SchematicManager.INSTANCE.createPlacement(buf);
		}
	}
}
