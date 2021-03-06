package io.github.samipourquoi.syncmatica.litematica;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.SchematicHolder;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.util.FileType;
import fi.dy.masa.malilib.gui.Message;
import io.github.samipourquoi.syncmatica.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.*;

// responsible for loading and keeping track of rendered syncmatic placements
// responsible for keeping track redirected litematic files (e.g. if the syncmatic was
// shared from this client)

public class LitematicManager {

    private static LitematicManager instance = null;


    // links syncmatic to schematic if it is rendered on the client
    // specific client
    private final Map<ServerPlacement, SchematicPlacement> rendering;
    private Collection<SchematicPlacement> preLoadList = new ArrayList<>();
    private Context context;

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

    // sets the active context for the gui side of things
    public void setActiveContext(final Context con) {
        if (con.isServer()) {
            throw new RuntimeException("Applied server context where client context was expected");
        }
        context = con;
        ScreenHelper.ifPresent((s) -> s.setActiveContext(con));
    }

    public Context getActiveContext() {
        return context;
    }

    // 1st case syncmatic placement is present and is now enabled from GUI
    // or another source
    public void renderSyncmatic(final ServerPlacement placement) {
        final String dimension = MinecraftClient.getInstance().getCameraEntity().getEntityWorld().getRegistryKey().getValue().toString();
        if (!dimension.equals(placement.getDimension())) {
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.player_dimension_mismatch"));
            context.getSyncmaticManager().updateServerPlacement(placement);
            return;
        }
        if (rendering.containsKey(placement)) {
            return;
        }
        final File file = context.getFileStorage().getLocalLitematic(placement);

        final LitematicaSchematic schematic = (LitematicaSchematic) SchematicHolder.getInstance().getOrLoad(file);

        if (schematic == null) {
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.failed_to_load", file.getAbsolutePath()));
            return;
        }

        final BlockPos origin = placement.getPosition();

        final SchematicPlacement litematicaPlacement = SchematicPlacement.createFor(schematic, origin, file.getName(), true, true);
        rendering.put(placement, litematicaPlacement);
        ((IIDContainer) litematicaPlacement).setServerId(placement.getId());
        if (litematicaPlacement.isLocked()) {
            litematicaPlacement.toggleLocked();
        }
        litematicaPlacement.setRotation(placement.getRotation(), null);
        litematicaPlacement.setMirror(placement.getMirror(), null);
        litematicaPlacement.toggleLocked();

        DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, true);
        context.getSyncmaticManager().updateServerPlacement(placement);
    }

    // 2nd case litematic placement is present but gets turned into ServerPlacement
    // removed side effects
    public ServerPlacement syncmaticFromSchematic(final SchematicPlacement schem) {
        if (rendering.containsValue(schem)) {
            // TODO: use the new ID for faster retrieval
            for (final ServerPlacement checkPlacement : rendering.keySet()) {
                if (rendering.get(checkPlacement) == schem) {
                    return checkPlacement;
                }
            }
            // theoretically not a possible branch that will be taken

            return null;
        }
        try {
            final File placementFile = schem.getSchematicFile();
            final FileType fileType = FileType.fromFile(placementFile);
            if (fileType == FileType.VANILLA_STRUCTURE || fileType == FileType.SCHEMATICA_SCHEMATIC) {
                ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.share_incompatible_schematic"));
                return null;
            } else if (fileType != FileType.LITEMATICA_SCHEMATIC) {
                ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.invalid_file"));
                return null;
            }

            final ServerPlacement placement = new ServerPlacement(UUID.randomUUID(), placementFile);
            // thanks miniHUD
            final String dimension = MinecraftClient.getInstance().getCameraEntity().getEntityWorld().getRegistryKey().getValue().toString();
            placement.move(dimension, schem.getOrigin(), schem.getRotation(), schem.getMirror());
            return placement;
        } catch (final Exception e) {
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.create_from_schematic", e.getMessage()));
        }
        return null;
    }


    public SchematicPlacement schematicFromSyncmatic(final ServerPlacement p) {
        return rendering.get(p);
    }

    // 3rd case litematic placement is loaded from file at startup or because the syncmatic got created from
    // it on this client
    // and the server gives confirmation that the schematic exists
    public void renderSyncmatic(final ServerPlacement placement, final SchematicPlacement litematicaPlacement, final boolean addToRendering) {
        if (rendering.containsKey(placement)) {
            return;
        }
        final IIDContainer modPlacement = (IIDContainer) litematicaPlacement;
        if (modPlacement.getServerId() != null && !modPlacement.getServerId().equals(placement.getId())) {
            return;
        }
        rendering.put(placement, litematicaPlacement);
        modPlacement.setServerId(placement.getId());

        if (litematicaPlacement.isLocked()) {
            litematicaPlacement.toggleLocked();
        }
        litematicaPlacement.setOrigin(placement.getPosition(), null);
        litematicaPlacement.setRotation(placement.getRotation(), null);
        litematicaPlacement.setMirror(placement.getMirror(), null);
        litematicaPlacement.toggleLocked();
        context.getSyncmaticManager().updateServerPlacement(placement);
        if (addToRendering) {
            DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, false);
        }
    }

    public void unrenderSyncmatic(final ServerPlacement placement) {
        if (!isRendered(placement)) {
            return;
        }
        DataManager.getSchematicPlacementManager().removeSchematicPlacement(rendering.get(placement));
        rendering.remove(placement);
        context.getSyncmaticManager().updateServerPlacement(placement);
    }

    public void updateRendered(final ServerPlacement placement) {
        if (!isRendered(placement)) {
            return;
        }
        final SchematicPlacement litematicaPlacement = rendering.get(placement);
        final boolean wasLocked = litematicaPlacement.isLocked();
        if (wasLocked) {
            litematicaPlacement.toggleLocked();
        }
        litematicaPlacement.setOrigin(placement.getPosition(), null);
        litematicaPlacement.setRotation(placement.getRotation(), null);
        litematicaPlacement.setMirror(placement.getMirror(), null);
        if (wasLocked) {
            litematicaPlacement.toggleLocked();
        }
    }

    public boolean isRendered(final ServerPlacement placement) {
        return rendering.containsKey(placement);
    }

    public boolean isSyncmatic(final SchematicPlacement schem) {
        return rendering.containsValue(schem);
    }

    // gets called by code mixed into litematicas loading stage
    // its responsible for keeping the litematics that got loaded in such a way
    // until a time where the server has told the client which syncmatics actually are still loaded
    public void preLoad(final SchematicPlacement schem) {
        if (context != null && context.isStarted()) {
            final UUID id = ((IIDContainer) schem).getServerId();
            final ServerPlacement p = context.getSyncmaticManager().getPlacement(id);
            if (isRendered(p)) {
                rendering.put(p, schem);
                DataManager.getSchematicPlacementManager().addSchematicPlacement(schem, false);
            }
        } else if (preLoadList != null) {
            preLoadList.add(schem);
        }
    }

    public void commitLoad() {
        final SyncmaticManager man = context.getSyncmaticManager();
        for (final SchematicPlacement schem : preLoadList) {
            final UUID id = ((IIDContainer) schem).getServerId();
            final ServerPlacement p = man.getPlacement(id);
            if (p != null) {
                if (context.getFileStorage().getLocalLitematic(p) != schem.getSchematicFile()) {
                    ((RedirectFileStorage) context.getFileStorage()).addRedirect(schem.getSchematicFile());
                }
                renderSyncmatic(p, schem, true);
            }
        }
        preLoadList = null;
    }

    public void unrenderSchematic(final LitematicaSchematic l) {
        rendering.entrySet().removeIf(e -> {
            if (e.getValue().getSchematic() == l) {
                context.getSyncmaticManager().updateServerPlacement(e.getKey());
                return true;
            }
            return false;
        });
    }

    public void unrenderSchematicPlacement(final SchematicPlacement placement) {
        final UUID id = ((IIDContainer) placement).getServerId();
        final ServerPlacement p = context.getSyncmaticManager().getPlacement(id);
        if (p != null) {
            unrenderSyncmatic(p);
        }
    }

    public ServerPosition getPlayerPosition() {
        if (MinecraftClient.getInstance().getCameraEntity() != null) {
            final BlockPos blockPos = MinecraftClient.getInstance().getCameraEntity().getBlockPos();
            final String dimensionId = getPlayerDimension();
            return new ServerPosition(blockPos, dimensionId);
        }
        return new ServerPosition(new BlockPos(0, 0, 0), ServerPosition.OVERWORLD_DIMENSION_ID);
    }

    public String getPlayerDimension() {
        if (MinecraftClient.getInstance().getCameraEntity() != null) {
            return MinecraftClient.getInstance().getCameraEntity().getEntityWorld().getRegistryKey().getValue().toString();
        } else {
            return ServerPosition.OVERWORLD_DIMENSION_ID;
        }
    }
}