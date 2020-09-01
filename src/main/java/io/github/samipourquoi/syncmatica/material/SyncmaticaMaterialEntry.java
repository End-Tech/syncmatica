package io.github.samipourquoi.syncmatica.material;

import java.util.function.Predicate;

public class SyncmaticaMaterialEntry {
	int amountRequired;
	int amountPresent;
	String claimedBy;
	
	public static final Unclaimed UNCLAIMED = new Unclaimed();
	public static final Unfinished UNFINISHED = new Unfinished();

	public boolean isClaimed() {
		return claimedBy != null;
	}
	
	public boolean isFinished() {
		return amountPresent >= amountRequired;
	}
	
	public static class Unclaimed implements Predicate<SyncmaticaMaterialEntry> {
		@Override
		public boolean test(SyncmaticaMaterialEntry arg0) {
			return !arg0.isClaimed();
		}
	}
	
	public static class Unfinished implements Predicate<SyncmaticaMaterialEntry> {
		@Override
		public boolean test(SyncmaticaMaterialEntry arg0) {
			return !arg0.isFinished();
		}
	}
}
