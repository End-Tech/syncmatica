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
import java.nio.file.Files;
import java.util.ArrayList;

public class SchematicManager {
	public static final SchematicManager INSTANCE = new SchematicManager();
	public final ArrayList<SchematicPlacement> schematics = new ArrayList<SchematicPlacement>();

	public SchematicManager() {
	}

	public void createPlacement(PacketByteBuf buf) {
		String name = buf.readString();
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		int rotation = buf.readInt();
		int mirror = buf.readInt();
		byte[] content = ByteBufUtil.getBytes(buf);

		File file = new File("./schematics/.sync/" + name + ".litematic");
		file.delete(); // If ever there is already the schematic

		System.out.println(name + x + y + z + rotation + mirror);

		try {
			Files.write(file.toPath(), content);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		LitematicaSchematic schematic = LitematicaSchematic.createFromFile(file.getParentFile(), file.getName());
		BlockPos origin = new BlockPos(x, y, z);

		SchematicPlacement placement = SchematicPlacement.createFor(schematic, origin, name, true, true);
		schematics.add(placement);

		placement.setRotation(BlockRotation.values()[rotation], null);
		placement.setMirror(BlockMirror.values()[mirror], null);
		placement.toggleLocked();

		DataManager.getSchematicPlacementManager().addSchematicPlacement(placement, false);
	}
}
