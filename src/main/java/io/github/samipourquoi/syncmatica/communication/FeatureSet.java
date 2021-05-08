package io.github.samipourquoi.syncmatica.communication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.samipourquoi.syncmatica.Feature;

// a class representing what kind of features a syncmatica instance supports
// or has enabled/disabled

public class FeatureSet {
	
	private static Map<String,FeatureSet> versionFeatures;	
	private Collection<Feature> features;

	public static FeatureSet fromVersionString(String version) {
		if (version.matches("^\\d+(\\.\\d+){2,}$")) {
			final int minSize = version.indexOf(".");
			while(version.length()>minSize) {
				if (versionFeatures.containsKey(version)) {
					return versionFeatures.get(version);
				}
				final int lastDot = version.lastIndexOf(".");
				version = version.substring(0, lastDot);
			}
		}
		return null;
	}
	
	public static FeatureSet fromString(String features) {
		FeatureSet featureSet = new FeatureSet(new ArrayList<>());
		for (String feature: features.split("\n")) {
			Feature f = Feature.fromString(feature);
			if (f != null) {
				featureSet.features.add(f);
			}
		}
		return featureSet;
	}
	
	public String toString() {
		StringBuilder output = new StringBuilder();
		boolean b = false;
		for (Feature feature: features) {
			output.append(b ? "\n"+feature.toString() : feature.toString());
			b = true;
		}
		return output.toString();
	}
	
	public FeatureSet(Collection<Feature> features) {
		this.features = features;
	}
	
	public boolean hasFeature(Feature f) {
		return features.contains(f);
	}
	
	static {
		versionFeatures = new HashMap<>();
		versionFeatures.put("0.1", new FeatureSet(Arrays.asList(Feature.CORE)));
	}

}
