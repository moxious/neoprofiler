package org.mitre.neoprofiler.profiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.NeoProperty;
import org.mitre.neoprofiler.profile.ParameterizedNeoProfile;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class LabelProfiler extends QueryRunner implements Profiler {
	String label = null;
	
	class LabelProfile extends ParameterizedNeoProfile {
		public LabelProfile(String label) { 
			name="NodeProfile"; 
			description="Profile of nodes labeled '" + label + "'";
			setParameter("label", label);
		}
	}
	
	public LabelProfiler(String label) {
		this.label = label.replaceAll("\\[", "").replaceAll("\\]", "");
	}
	
	public NeoProfile run(NeoProfiler parent) {		
		NeoProfile p = new LabelProfile(label);
		
		Object result = runQuerySingleResult(parent, "match (n:`" + label +"`) return count(n) as c", "c");
		p.addObservation("Total nodes", ""+result);
		
		int sampleSize = 100;
		List<Object>nodeSamples = runQueryMultipleResult(parent, "match (n:`" + label + "`) return n as instance limit " + sampleSize, 
				"instance");
		
		try ( Transaction tx = parent.getDB().beginTx() ) {
			HashSet<NeoProperty> props = new HashSet<NeoProperty>();
			HashMap<String,Integer> seen = new HashMap<String,Integer>();
			
			for(Object ns : nodeSamples) { 				
				for(NeoProperty prop : getSampleProperties(parent, (Node)ns)) {
					String key = prop.toString();
					
					// Increment counter.
					if(!seen.containsKey(key)) { seen.put(key, 1); }
					else { seen.put(key, seen.get(key) + 1); continue; } 					
					
					props.add(prop);
				}							
			}
			
			// If a property was seen in some (but not all) samples, then it's optional.
			for(NeoProperty prop : props) { 
				int count = seen.get(prop.toString());
				System.out.println("For label " + label + " property " + prop + " seen " + count + " of " + sampleSize + " times.");
				if(count < sampleSize) prop.setOptional(true);
				else prop.setOptional(false); 
			}
			
			p.addObservation("Sample properties", props);
		} // End try
			
		List<Object>outbound = runQueryMultipleResult(parent, 
				"match (n:" + label + ")-[r]->m where n <> m return distinct(type(r)) as outbound", "outbound");
		p.addObservation("Outbound relationship types", outbound);
		
		List<Object>inbound = runQueryMultipleResult(parent, 
				"match (n:" + label + ")<-[r]-m where n <> m return distinct(type(r)) as outbound", "outbound");
		p.addObservation("Inbound relationship types", inbound); 
		
		// TODO Auto-generated method stub
		return p;
	}
}
