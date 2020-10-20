package io.github.samipourquoi.syncmatica;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import io.github.samipourquoi.syncmatica.material.SyncmaticaMaterialList;
import io.github.samipourquoi.syncmatica.util.SyncmaticaUtil;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class ServerPlacement {
	
	private final UUID id;
	
	private final String fileName;
    private final byte[] hashValue;
    
    private ServerPosition origin;
    private BlockRotation rotation;
    private BlockMirror mirror;
    
    private SyncmaticaMaterialList matList;
    
    public ServerPlacement(UUID id, String fileName, byte[] hashValue) {
    	this.id = id;
    	this.fileName = fileName;
    	this.hashValue = hashValue;
    }
    
    public ServerPlacement(UUID id, File file) {
    	this(id, removeExtension(file), generateHash(file));
    }
    
    public UUID getId() {return id;}
    
    public String getFileName() {return fileName;}
    public byte[] getHash() {return hashValue;}
    
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
    
    private static byte[] generateHash(File file) {
		byte[] hash = null;
		try {
			hash = SyncmaticaUtil.createChecksum(new FileInputStream(file));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return hash;
    }
    
}
