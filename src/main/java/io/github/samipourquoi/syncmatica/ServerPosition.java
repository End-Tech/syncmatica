package io.github.samipourquoi.syncmatica;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.math.BlockPos;

public class ServerPosition {
	private final BlockPos position;
	private final String dimensionId;
	
	public ServerPosition(BlockPos pos, String dim) {
		position = pos;
		dimensionId = dim;
	}
	
	public BlockPos getBlockPosition() {
		return position;
	}
	
	public String getDimensionId() {
		return dimensionId;
	}
	
	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		JsonArray arr = new JsonArray();
		arr.add(new JsonPrimitive(position.getX()));
		arr.add(new JsonPrimitive(position.getY()));
		arr.add(new JsonPrimitive(position.getZ()));
		obj.add("position", arr);
		obj.add("dimension", new JsonPrimitive(dimensionId));
		return obj;
	}
	
	public static ServerPosition fromJson(JsonObject obj) {
		if (obj.has("position") && obj.has("dimension")) {
			int x,y,z; 
			JsonArray arr = obj.get("position").getAsJsonArray();
			x = arr.get(0).getAsInt();
			y = arr.get(1).getAsInt();
			z = arr.get(2).getAsInt();
			BlockPos pos = new BlockPos(x, y, z);
			return new ServerPosition(pos, obj.get("dimension").getAsString());
		}
		// could try to throw an exception and catch it or maybe log the thing idk
		// TODO: Decide
		return null;
	}
}
