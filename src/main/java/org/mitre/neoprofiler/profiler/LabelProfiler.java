package org.mitre.neoprofiler.profiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.LabelProfile;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.NeoProperty;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class LabelProfiler extends QueryRunner implements Profiler {
	private static final Logger log = Logger.getLogger(LabelProfiler.class.getName());
	String label = null;
		
	public LabelProfiler(String label) {
		if(label == null || "".equals(label)) { 
			log.severe("Invalid input label: '" + label + "'");
		}
		
		this.label = label.replaceAll("\\[", "").replaceAll("\\]", "");
		
		if("".equals(this.label)) { 
			log.severe("Invalid processed label '" + label + "' => '" + this.label + "'");
		}
	}
	
	public NeoProfile run(NeoProfiler parent) {		
		LabelProfile p = new LabelProfile(label);
		
		Object result = runQuerySingleResult(parent, "match (n:`" + label +"`) return count(n) as c", "c");
		p.addObservation("Total nodes", ""+result);
		
		int sampleSize = (Integer)p.getParameter("sampleSize");
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
				
				parent.schedule(new LabelPropertyProfiler(label, prop.getName()));
			}
			
			p.addObservation(NeoProfile.OB_SAMPLE_PROPERTIES, props);
		} // End try
			
		List<Object>outbound = runQueryMultipleResult(parent, 
				"match (n:`" + label + "`)-[r]->m where n <> m return distinct(type(r)) as outbound", "outbound");
		
		if(outbound.isEmpty()) outbound.add(NeoProfile.OB_VALUE_NA);
		p.addObservation(LabelProfile.OB_OUTBOUND_RELATIONSHIP_TYPES, outbound);
		
		List<Object>inbound = runQueryMultipleResult(parent, 
				"match (n:`" + label + "`)<-[r]-m where n <> m return distinct(type(r)) as outbound", "outbound");
		
		if(inbound.isEmpty()) inbound.add(NeoProfile.OB_VALUE_NA);
		p.addObservation(LabelProfile.OB_INBOUND_RELATIONSHIP_TYPES, inbound); 
		
		// TODO Auto-generated method stub
		return p;
	}
}
