package io.github.samipourquoi.syncmatica.mixin;

import io.github.samipourquoi.syncmatica.communication.PacketType;
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Inject(method = "onPlayerConnect", at = @At("TAIL"))
	private void sendLitematics(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		File folder = new File("./syncmatics");
		for (File litematic : Objects.requireNonNull(folder.listFiles((dir, name) -> name.matches("[a-zA-Z]+_-?[0-9]+,-?[0-9]+,-?[0-9]+_(NONE|CW_90|CW_180|CCW_90)_(NONE|LEFT_RIGHT|FRONT_BACK).litematic")))) {
			try {
				byte[] bytes = Files.readAllBytes(litematic.toPath());
				PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());

				String[] parts = litematic.getName().split("_");
				String[] blockPos = parts[1].split(",");

				// Name
				buffer.writeString(parts[0]);

				// Coordinates
				buffer.writeInt(Integer.parseInt(blockPos[0]));
				buffer.writeInt(Integer.parseInt(blockPos[1]));
				buffer.writeInt(Integer.parseInt(blockPos[2]));

				// Rotation and mirroring
				buffer.writeInt(parseBlockRotation(parts[2]).ordinal());
				buffer.writeInt(parseBlockMirroring(parts[3]).ordinal());

				// Content
				buffer.writeBytes(bytes);

				player.networkHandler.sendPacket(new CustomPayloadS2CPacket(PacketType.REGISTER.IDENTIFIER, buffer));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static BlockRotation parseBlockRotation(String rotation) {
		switch (rotation) {
			case "NONE":
			default:
				return BlockRotation.NONE;
			case "CW_90":
				return BlockRotation.CLOCKWISE_90;
			case "CW_180":
				return BlockRotation.CLOCKWISE_180;
			case "CCW_90":
				return BlockRotation.COUNTERCLOCKWISE_90;
		}
	}

	private static BlockMirror parseBlockMirroring(String mirroring) {
		switch (mirroring) {
			case "NONE":
			default:
				return BlockMirror.NONE;
			case "LEFT_RIGHT":
				return BlockMirror.LEFT_RIGHT;
			case "FRONT_BACK":
				return BlockMirror.FRONT_BACK;
		}
	}
}
