package ch.endte.syncmatica.extended_core;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class SubRegionData {
    private boolean isModified;
    private Map<String, SubRegionPlacementModification> modificationData; // is null when isModified is false

    public SubRegionData() {
        this(false, null);
    }

    public SubRegionData(final boolean isModified, final Map<String, SubRegionPlacementModification> modificationData) {
        this.isModified = isModified;
        this.modificationData = modificationData;
    }

    public void reset() {
        isModified = false;
        modificationData = null;
    }

    public void modify(
            final String name,
            final BlockPos position,
            final BlockRotation rotation,
            final BlockMirror mirror
    ) {
        modify(
                new SubRegionPlacementModification(
                        name,
                        position,
                        rotation,
                        mirror
                )
        );
    }

    public void modify(final SubRegionPlacementModification subRegionPlacementModification) {
        isModified = true;
        if (modificationData == null) {
            modificationData = new HashMap<>();
        }
        modificationData.put(subRegionPlacementModification.name, subRegionPlacementModification);
    }

    public boolean isModified() {
        return isModified;
    }

    public Map<String, SubRegionPlacementModification> getModificationData() {
        return modificationData;
    }

    public JsonObject toJson() {
        final JsonObject obj = new JsonObject();

        obj.add("isModified", new JsonPrimitive(isModified));

        if (!isModified) {

            return obj;
        }

        obj.add("modificationData", modificationDataToJson());

        return obj;
    }

    private JsonElement modificationDataToJson() {
        final JsonArray arr = new JsonArray();

        for (final Map.Entry<String, SubRegionPlacementModification> entry : modificationData.entrySet()) {
            arr.add(entry.getValue().toJson());
        }

        return arr;
    }

    public static SubRegionData fromJson(final JsonObject obj) {
        if (obj.has("isModified")) {
            final SubRegionData newSubRegionData = new SubRegionData();

            newSubRegionData.isModified = obj.get("isModified").getAsBoolean();

            if (newSubRegionData.isModified) {
                for (final JsonElement modification : obj.get("modificationData").getAsJsonArray()) {
                    newSubRegionData.modify(SubRegionPlacementModification.fromJson(modification.getAsJsonObject()));
                }
            }

            return newSubRegionData;
        }

        return null;
    }

    @Override
    public String toString() {
        if (!isModified) {

            return "[]";
        }

        return modificationData == null ? "[ERROR:null]" : modificationData.toString();
    }
}
