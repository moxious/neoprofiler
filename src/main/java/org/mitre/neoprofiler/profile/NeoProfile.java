package org.mitre.neoprofiler.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class NeoProfile {
	protected String name;
	protected String description;
	protected HashMap<String,Object> observations = new HashMap<String,Object>();
	
	public String getName() { return name; } 
	public String getDescription() { return description; } 
	public Map<String,Object> getObservations() { return observations; } 
	public void addObservation(String name, Object observation) { observations.put(name, observation); } 
	
	public String toString() { 
		StringBuffer b = new StringBuffer("");
		b.append(name + ": " + description + "\n");
		
		ArrayList<String> keys = new ArrayList<String>(getObservations().keySet());
		Collections.sort(keys);
		
		for(String key : keys) {
			b.append(key + ": " + observations.get(key) + "\n"); 
		}
		
		return b.toString();
	} // End toString
}
