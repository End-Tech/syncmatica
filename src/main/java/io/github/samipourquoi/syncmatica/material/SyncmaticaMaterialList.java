package io.github.samipourquoi.syncmatica.material;

import java.util.ArrayList;
import java.util.Optional;

public class SyncmaticaMaterialList {
	private ArrayList<SyncmaticaMaterialEntry> list;
	
	public SyncmaticaMaterialEntry getUnclaimedEntry() {
		Optional<SyncmaticaMaterialEntry> unclaimed = list.parallelStream().filter(SyncmaticaMaterialEntry.UNFINISHED).filter(SyncmaticaMaterialEntry.UNCLAIMED).findFirst();
		if (unclaimed.isPresent()) {
			return unclaimed.get();
		}
		return null;
	}
}
