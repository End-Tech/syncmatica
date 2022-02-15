package ch.endte.syncmatica.litematica;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;

public interface MovingFinisher {
    void onFinishedMoving(String subRegionName, SchematicPlacementManager manager);
}
