package io.github.samipourquoi.syncmatica.material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import io.github.samipourquoi.syncmatica.ServerPosition;

public class SyncmaticaMaterialList {
	private ArrayList<SyncmaticaMaterialEntry> list;
	private ServerPosition deliveryPoint;
	
	public SyncmaticaMaterialEntry getUnclaimedEntry() {
		Optional<SyncmaticaMaterialEntry> unclaimed = list.parallelStream().filter(SyncmaticaMaterialEntry.UNFINISHED).filter(SyncmaticaMaterialEntry.UNCLAIMED).findFirst();
		if (unclaimed.isPresent()) {
			return unclaimed.get();
		}
		return null;
	}
	
	public Collection<DeliveryPosition> getDeliveryPosition(SyncmaticaMaterialEntry entry) {
		if (!list.contains(entry)) {
			throw new IllegalArgumentException();
		}
		DeliveryPosition delivery = new DeliveryPosition(deliveryPoint.getBlockPosition(), deliveryPoint.getDimensionId(), entry.getAmountMissing()); 
		ArrayList<DeliveryPosition> deliveryList = new ArrayList<DeliveryPosition>();
		deliveryList.add(delivery);
		return deliveryList;
	}
}
