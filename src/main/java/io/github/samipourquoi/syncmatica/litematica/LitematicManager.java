package io.github.samipourquoi.syncmatica.litematica;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

// responsible for loading and keeping track of rendered syncmatic placements
// responsible for keeping track redirected litematic files (e.g. if the syncmatic was
// shared from this client)

public class LitematicManager {
	
	private static LitematicManager instance = null;
	
	// links syncmatic to schematic if it is rendered on the client
	// specific client
	private final Map<ServerPlacement, SchematicPlacement> rendering;
	
	public static LitematicManager getInstance() {
		if (instance == null) {
			instance = new LitematicManager();
		}
		return instance;
	}
	
	public static void clear() {
		instance = null;
	}

	private LitematicManager() {
		rendering = new HashMap<>();
	}
	
	// 1st case syncmatic placement is present and is now enabled from GUI
	// or another source
	public void renderSyncmatic(ServerPlacement placement) {
		if (rendering.containsKey(placement)) {
			return;
		}
		File file = Syncmatica.getFileStorage().getLocalLitematic(placement);

		LitematicaSchematic schematic = LitematicaSchematic.createFromFile(file.getParentFile(), file.getName());
		
		if (schematic == null) {
			throw new RuntimeException("Could not create schematic from file");
		}
		
		BlockPos origin = placement.getPosition();

		SchematicPlacement litematicaPlacement = SchematicPlacement.createFor(schematic, origin, file.getName(), true, true);
		rendering.put(placement, litematicaPlacement);

		litematicaPlacement.setRotation(placement.getRotation(), null);
		litematicaPlacement.setMirror(placement.getMirror(), null);
		litematicaPlacement.toggleLocked();

		DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, true);
	}
	
	// 2nd case litematic placement is present but gets turned into ServerPlacement
	// removed side effects
	public ServerPlacement syncmaticFromSchematic(SchematicPlacement schem) {
		if (rendering.containsValue(schem)) {
			// FIXME: this seems like an anti-pattern
			for (ServerPlacement checkPlacement: rendering.keySet()) {
				if (rendering.get(checkPlacement) == schem) {
					return checkPlacement;
				}
			}
			// theoretically not a possible branch that will be taken
			
			return null;
		}
		File placementFile = schem.getSchematicFile();
		ServerPlacement placement = new ServerPlacement(UUID.randomUUID(), placementFile);
		placement.move("somewhere in minecraft", schem.getOrigin(), schem.getRotation(), schem.getMirror());
		return placement;
	}
	
	// 3rd case litematic placement is loaded from file at startup or because the syncmatic got created from
	// it on this client
	// and the server gives confirmation that the schematic exists
	public void renderSyncmatic(ServerPlacement placement, SchematicPlacement litematicaPlacement, boolean addToRendering) {
		if (rendering.containsKey(placement)) {
			return;
		}
		rendering.put(placement, litematicaPlacement);
		
		LogManager.getLogger(ClientPlayNetworkHandler.class).info("Rendered Placement");

		litematicaPlacement.setOrigin(placement.getPosition(), null);
		litematicaPlacement.setRotation(placement.getRotation(), null);
		litematicaPlacement.setMirror(placement.getMirror(), null);
		if (!litematicaPlacement.isLocked()) {
			litematicaPlacement.toggleLocked();
		}
		if (addToRendering) {
			DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, false);
		}
	}
	
	public void unrenderSyncmatic(ServerPlacement placement) {
		if (!isRendered(placement)) {
			return;
		}
		DataManager.getSchematicPlacementManager().removeSchematicPlacement(rendering.get(placement));
		rendering.remove(placement);
	}
	
	public boolean isRendered(ServerPlacement placement) {
		return rendering.containsKey(placement);
	}
	
	public boolean isSyncmatic(SchematicPlacement schem) {
		return rendering.containsValue(schem);
	}
	
}
