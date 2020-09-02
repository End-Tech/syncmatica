package io.github.samipourquoi.syncmatica;

import java.util.UUID;

import io.github.samipourquoi.syncmatica.material.SyncmaticaMaterialList;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class SyncmaticaServerPlacement {
	
	private final UUID id;
	
	private final String fileName;
    private final byte[] hashValue;
    
    private String dimensionId;
	private BlockPos origin;
    private BlockRotation rotation;
    private BlockMirror mirror;
    
    private SyncmaticaMaterialList matList;
    
    public SyncmaticaServerPlacement(UUID id, String fileName, byte[] hashValue) {
    	this.id = id;
    	this.fileName = fileName;
    	this.hashValue = hashValue;
    }
    
    public UUID getId() {return id;}
    
    public String getFileName() {return fileName;}
    public byte[] getHash() {return hashValue;}
    
    public String getDimension() {return dimensionId;}
    public BlockPos getOrigin() {return origin;}
    public BlockRotation getRotation() {return rotation;}
    public BlockMirror getMirror() {return mirror;}
    public SyncmaticaServerPlacement move(String dimensionId, BlockPos origin, BlockRotation rotation, BlockMirror mirror) {
    	this.dimensionId = dimensionId;
    	this.origin = origin;
    	this.rotation = rotation;
    	this.mirror = mirror;
    	return this;
    }
    
    public SyncmaticaMaterialList getMaterialList() {return matList;}
    public SyncmaticaServerPlacement setMaterialList(SyncmaticaMaterialList matList) {
    	if (this.matList != null) {
    		this.matList = matList;
    	}
    	return this;
    }
    
    
}
