package io.github.samipourquoi.syncmatica;

public enum Feature {
	CORE, // every feature that's part of 0.1.0 - it doesn't make sense to divide those further since compatability with 0.0 of future versions
	// cannot be maintained and the version is very alpha.
	FEATURE; // the possibility of reporting on ones own features during version exchange
	// MODIFY; // commands to modify the placement of a syncmatic placement on the server
	
	public static Feature fromString(String s) {
		for (Feature f: Feature.values()) {
			if (f.toString().equals(s)) {
				return f;
			}
		}
		return null;
	}
}
