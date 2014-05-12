package org.mitre.neoprofiler.profiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.NeoProperty;
import org.mitre.neoprofiler.profile.RelationshipTypeProfile;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * Profiler for a particular relationship type.
 * @author moxious
 */
public class RelationshipTypeProfiler extends QueryRunner implements Profiler {
	String type = null;
	
	public RelationshipTypeProfiler(String type) {
		this.type = type;
	}
	
	public NeoProfile run(NeoProfiler parent) {		
		RelationshipTypeProfile p = new RelationshipTypeProfile(type);
		
		int sampleSize = (Integer)p.getParameter("sampleSize");
		
		Object result = runQuerySingleResult(parent, "match n-[r:`" + type + "`]->m return count(r) as c", "c");
		p.addObservation("Total relationships", ""+result);
		
		Map<String,List<Object>> ret = runQueryComplexResult(parent,
				"match n-[r:`" + type + "`]->m return n as left, m as right, r as rel limit " + sampleSize, 
				"right", "left", "rel");
		
		try ( Transaction tx = parent.getDB().beginTx() ) {
			HashSet<NeoProperty> props = new HashSet<NeoProperty>();
			HashMap<String,Integer> seen = new HashMap<String,Integer>();
			
			for(Object r : ret.get("rel")) { 				
				for(NeoProperty prop : getSampleProperties(parent, (Relationship)r)) {
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
				System.out.println("For relationship " + type + " property " + prop + " seen " + count + " of " + sampleSize + " times.");
				if(count < sampleSize) prop.setOptional(true);
				else prop.setOptional(false); 
			}
			
			p.addObservation(NeoProfile.OB_SAMPLE_PROPERTIES, props);
			
			HashSet<String> labels = new HashSet<String>();
			
			for(Object headNode : ret.get("left")) {
				Iterator<Label> headLabels = ((Node)headNode).getLabels().iterator();
				while(headLabels.hasNext()) labels.add(headLabels.next().name());				
			}
			
			p.addObservation(RelationshipTypeProfile.OB_DOMAIN, labels); 
						
			labels = new HashSet<String>();
			for(Object tailNode : ret.get("right")) {
				Iterator<Label> tailLabels = ((Node)tailNode).getLabels().iterator();
				while(tailLabels.hasNext()) labels.add(tailLabels.next().name());				
			}
			
			p.addObservation(RelationshipTypeProfile.OB_RANGE, labels); 			
		} // End try
			
		return p;
	}
}
