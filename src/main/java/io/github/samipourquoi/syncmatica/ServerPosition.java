package io.github.samipourquoi.syncmatica;

import net.minecraft.util.math.BlockPos;

public class ServerPosition {
	private final BlockPos position;
	private final String dimensionId;
	
	public ServerPosition(BlockPos pos, String dim) {
		position = pos;
		dimensionId = dim;
	}
	
	public BlockPos getBlockPosition() {
		return position;
	}
	
	public String getDimensionId() {
		return dimensionId;
	}
}
