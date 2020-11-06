package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.github.samipourquoi.syncmatica.material.SyncmaticaMaterialList;
import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class ServerPlacement {
	
	private final UUID id;
	
	private final String fileName;
    private final UUID hashValue; //UUID for the file contents
    // UUID since easier to transmit compare etc.
    
    private ServerPosition origin;
    private BlockRotation rotation;
    private BlockMirror mirror;
    
    private SyncmaticaMaterialList matList;
    
    public ServerPlacement(UUID id, String fileName, UUID hashValue) {
    	this.id = id;
    	this.fileName = fileName;
    	this.hashValue = hashValue;
    }
    
    public ServerPlacement(UUID id, File file) {
    	this(id, removeExtension(file), generateHash(file));
    }
    
    public UUID getId() {return id;}
    
    public String getName() {return fileName;}
    public UUID getHash() {return hashValue;}
    
    public String getDimension() {return origin.getDimensionId();}
    public BlockPos getPosition() {return origin.getBlockPosition();}
    public ServerPosition getOrigin() {return origin;}
    public BlockRotation getRotation() {return rotation;}
    public BlockMirror getMirror() {return mirror;}
    
    public ServerPlacement move(String dimensionId, BlockPos origin, BlockRotation rotation, BlockMirror mirror) {
    	move(new ServerPosition(origin, dimensionId), rotation, mirror);
    	return this;
    }
    public ServerPlacement move(ServerPosition origin, BlockRotation rotation, BlockMirror mirror) {
    	this.origin = origin;
    	this.rotation = rotation;
    	this.mirror = mirror;
    	return this;
    }
    
    public SyncmaticaMaterialList getMaterialList() {return matList;}
    public ServerPlacement setMaterialList(SyncmaticaMaterialList matList) {
    	if (this.matList != null) {
    		this.matList = matList;
    	}
    	return this;
    }
    
    private static String removeExtension(File file) {
    	// source stackoverflow
    	String fileName = file.getName();
    	int pos = fileName.lastIndexOf(".");
    	return fileName.substring(0, pos);
    }
    
    private static UUID generateHash(File file) {
		UUID hash = null;
		try {
			hash = SyncmaticaUtil.createChecksum(new FileInputStream(file));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return hash;
    }
    
    public JsonObject toJson() {
    	JsonObject obj = new JsonObject();
    	obj.add("id", new JsonPrimitive(id.toString()));
    	
    	obj.add("file_name", new JsonPrimitive(fileName));
    	obj.add("hash", new JsonPrimitive(hashValue.toString()));
    	
    	obj.add("origin", origin.toJson());
    	obj.add("rotation", new JsonPrimitive(rotation.name()));
    	obj.add("mirror", new JsonPrimitive(mirror.name()));
    	
    	return obj;
    }
    
    public static ServerPlacement fromJson(JsonObject obj) {
    	if (obj.has("id") 
    			&& obj.has("file_name")
    			&& obj.has("hash")
    			&& obj.has("origin")
    			&& obj.has("rotation")
    			&& obj.has("mirror")) {
    		UUID id = UUID.fromString(obj.get("id").getAsString());
			String name = obj.get("file_name").getAsString();
			UUID hashValue = UUID.fromString(obj.get("hash").getAsString());
			
			ServerPlacement newPlacement = new ServerPlacement(id, name, hashValue);
			
			ServerPosition pos = ServerPosition.fromJson(obj.get("origin").getAsJsonObject());
			if (pos == null) {
				return null;
			}
			newPlacement.origin = pos;
			newPlacement.rotation = BlockRotation.valueOf(obj.get("rotation").getAsString());
			newPlacement.mirror = BlockMirror.valueOf(obj.get("mirror").getAsString());
		
			return newPlacement;
    	}
    	return null;
    }
    
    
}
