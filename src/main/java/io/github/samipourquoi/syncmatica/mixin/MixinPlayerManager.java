package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.packets.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Inject(method = "onPlayerConnect", at = @At("TAIL"))
	private void sendLitematics(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		File litematic = new File("./syncmatics/test.litematic");
		try {
			byte[] bytes = Files.readAllBytes(litematic.toPath());
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
			buffer.writeInt(BlockRotation.CLOCKWISE_90.ordinal());
			buffer.writeInt(BlockMirror.NONE.ordinal());
			buffer.writeBytes(bytes);

			player.networkHandler.sendPacket(new CustomPayloadS2CPacket(PacketType.REGISTER.IDENTIFIER, buffer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
