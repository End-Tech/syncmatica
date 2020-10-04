package io.github.samipourquoi.syncmatica.material;

import io.github.samipourquoi.syncmatica.ServerPosition;
import net.minecraft.util.math.BlockPos;

public class DeliveryPosition extends ServerPosition {
	
	private int amount;
	
	public DeliveryPosition(BlockPos pos, String dim, int amount) {
		super(pos, dim);
		this.amount = amount;
	}

	public int getAmount() {return this.amount;}
}
