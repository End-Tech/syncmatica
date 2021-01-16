package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SyncmaticManager {
	private final Map<UUID, ServerPlacement> schematics = new HashMap<>();
	private final Collection<Consumer<ServerPlacement>> consumers = new ArrayList<>();
	
	Context context;
	
	public void setContext(Context con) {
		if (context == null) {
			context = con;
		} else {
			throw new RuntimeException("Duplicate Context assignment");
		}
	}
	
	public void addPlacement(ServerPlacement placement) {
		schematics.put(placement.getId(), placement);
		updateServerPlacement(placement);
	}
	
	public ServerPlacement getPlacement(UUID id) {
		return schematics.get(id);
	}
	
	public Collection<ServerPlacement> getAll() {
		return schematics.values();
	}
	
	public void removePlacement(ServerPlacement placement) {
		schematics.remove(placement.getId());
		updateServerPlacement(placement);
	}
	
	public void addServerPlacementConsumer(Consumer<ServerPlacement> consumer) {
		consumers.add(consumer);
	}
	
	public void removeServerPlacementConsumer(Consumer<ServerPlacement> consumer) {
		consumers.remove(consumer);
	}
	
	public void updateServerPlacement(ServerPlacement updated) {
		for (Consumer<ServerPlacement> consumer: consumers) {
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
		JsonObject obj = new JsonObject();
		JsonArray arr = new JsonArray();
		
		for (ServerPlacement p : getAll()) {
			arr.add(p.toJson());
		}
		
		obj.add("placements", arr);
		context.getConfigFolder().mkdirs();
		File f = new File(context.getConfigFolder(), "placements.json");
		
		FileWriter writer = null;
        try {
        	writer = new FileWriter(f);
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	private void loadServer() {
		File f = new File(context.getConfigFolder(), "placements.json");
        if (f != null && f.exists() && f.isFile() && f.canRead()) {
        	JsonElement element = null;
            try {
                JsonParser parser = new JsonParser();
                FileReader reader = new FileReader(f);

                element = parser.parse(reader);
                reader.close();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
            	JsonObject obj = element.getAsJsonObject();
            	if (!obj.has("placements")) {return;}
            	JsonArray arr = obj.getAsJsonArray("placements");
            	for (JsonElement elem : arr) {
            		ServerPlacement p = ServerPlacement.fromJson(elem.getAsJsonObject());
            		addPlacement(p);
            	}
            	
            } catch (IllegalStateException e) {
            	e.printStackTrace();
            } catch (NullPointerException e) {
            	e.printStackTrace();
            }
        }
	}
	
}
