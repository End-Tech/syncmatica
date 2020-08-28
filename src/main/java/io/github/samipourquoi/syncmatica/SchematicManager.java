package io.github.samipourquoi.syncmatica;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class SchematicManager {
	public static final SchematicManager INSTANCE = new SchematicManager();

	public SchematicManager() {
	}

	public void createPlacement(PacketByteBuf buf) {
		File file = new File("./schematics/test.litematic");
		int rotation = buf.readInt();
		int mirror = buf.readInt();
		byte[] bytes = ByteBufUtil.getBytes(buf);

		try {
			Files.write(file.toPath(), bytes);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		LitematicaSchematic schematic = LitematicaSchematic.createFromFile(file.getParentFile(), file.getName());
		BlockPos origin = new BlockPos(0, 100, 0);
		SchematicPlacement placement = SchematicPlacement.createFor(schematic, origin, "Schematic", true, true);
		placement.setRotation(BlockRotation.values()[rotation], null);
		placement.setMirror(BlockMirror.values()[mirror], null);
		placement.toggleLocked();
		DataManager.getSchematicPlacementManager().addSchematicPlacement(placement, false);
	}
}
