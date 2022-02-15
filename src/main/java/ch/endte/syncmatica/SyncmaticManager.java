package ch.endte.syncmatica;

import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class SyncmaticManager {
    public static final String PLACEMENTS_JSON_KEY = "placements";
    private final Map<UUID, ServerPlacement> schematics = new HashMap<>();
    private final Collection<Consumer<ServerPlacement>> consumers = new ArrayList<>();

    Context context;

    public void setContext(final Context con) {
        if (context == null) {
            context = con;
        } else {
            throw new Context.DuplicateContextAssignmentException("Duplicate Context assignment");
        }
    }

    public void addPlacement(final ServerPlacement placement) {
        schematics.put(placement.getId(), placement);
        updateServerPlacement(placement);
    }

    public ServerPlacement getPlacement(final UUID id) {
        return schematics.get(id);
    }

    public Collection<ServerPlacement> getAll() {
        return schematics.values();
    }

    public void removePlacement(final ServerPlacement placement) {
        schematics.remove(placement.getId());
        updateServerPlacement(placement);
    }

    public void addServerPlacementConsumer(final Consumer<ServerPlacement> consumer) {
        consumers.add(consumer);
    }

    public void removeServerPlacementConsumer(final Consumer<ServerPlacement> consumer) {
        consumers.remove(consumer);
    }

    public void updateServerPlacement(final ServerPlacement updated) {
        for (final Consumer<ServerPlacement> consumer : consumers) {
            consumer.accept(updated);
        }
    }

    public void startup() {
        if (context.isServer()) {
            loadServer();
        }
    }

    public void shutdown() {
        if (context.isServer()) {
            saveServer();
        }
    }

    private void saveServer() {
        final JsonObject obj = new JsonObject();
        final JsonArray arr = new JsonArray();

        for (final ServerPlacement p : getAll()) {
            arr.add(p.toJson());
        }

        obj.add(PLACEMENTS_JSON_KEY, arr);
        final File f = new File(context.getConfigFolder(), "placements.json");

        try (final FileWriter writer = new FileWriter(f)) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void loadServer() {
        final File f = new File(context.getConfigFolder(), "placements.json");
        if (f.exists() && f.isFile() && f.canRead()) {
            JsonElement element = null;
            try {
                final JsonParser parser = new JsonParser();
                final FileReader reader = new FileReader(f);

                element = parser.parse(reader);
                reader.close();

            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (element == null) {


                return;
            }
            try {
                final JsonObject obj = element.getAsJsonObject();
                if (obj == null || !obj.has(PLACEMENTS_JSON_KEY)) {
                    return;
                }
                final JsonArray arr = obj.getAsJsonArray(PLACEMENTS_JSON_KEY);
                for (final JsonElement elem : arr) {
                    final ServerPlacement p = ServerPlacement.fromJson(elem.getAsJsonObject(), context);
                    addPlacement(p);
                }

            } catch (final IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

}
