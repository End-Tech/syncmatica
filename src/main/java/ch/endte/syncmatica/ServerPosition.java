package ch.endte.syncmatica;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.BlockPos;

public class ServerPosition {
    private final BlockPos position;
    private final String dimensionId;

    public static final String NETHER_DIMENSION_ID = "minecraft:the_nether";
    public static final String OVERWORLD_DIMENSION_ID = "minecraft:overworld";

    public ServerPosition(final BlockPos pos, final String dim) {
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
        final JsonObject obj = new JsonObject();
        final JsonArray arr = new JsonArray();
        arr.add(new JsonPrimitive(position.getX()));
        arr.add(new JsonPrimitive(position.getY()));
        arr.add(new JsonPrimitive(position.getZ()));
        obj.add("position", arr);
        obj.add("dimension", new JsonPrimitive(dimensionId));
        return obj;
    }

    public static ServerPosition fromJson(final JsonObject obj) {
        if (obj.has("position") && obj.has("dimension")) {
            final int x;
            final int y;
            final int z;
            final JsonArray arr = obj.get("position").getAsJsonArray();
            x = arr.get(0).getAsInt();
            y = arr.get(1).getAsInt();
            z = arr.get(2).getAsInt();
            final BlockPos pos = new BlockPos(x, y, z);
            return new ServerPosition(pos, obj.get("dimension").getAsString());
        }
        // could try to throw an exception and catch it or maybe log the thing idk
        // TODO: Decide
        return null;
    }
}
