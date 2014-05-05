package org.mitre.neoprofiler.profiler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Profiler for a particular relationship type.
 * @author moxious
 */
public class RelationshipTypeProfiler extends QueryRunner implements Profiler {
	String type = null;
	
	class RelationshipTypeProfile extends NeoProfile {
		public RelationshipTypeProfile(String type) { 
			name="RelationshipTypeProfile"; description="Profile of relationships of type '" + type + "'";
		}
	}
	
	public RelationshipTypeProfiler(String type) {
		this.type = type;
	}
	
	public NeoProfile run(NeoProfiler parent) {		
		NeoProfile p = new RelationshipTypeProfile(type);
		
		Object result = runQuerySingleResult(parent, "match n-[r:`" + type + "`]->m return count(r) as c", "c");
		p.addObservation("Total relationships", ""+result);
		
		Map<String,List<Object>> ret = runQueryComplexResult(parent,
				"match n-[r:`" + type + "`]->m return n as left, m as right limit 50", 
				"right", "left");
		
		try ( Transaction tx = parent.getDB().beginTx() ) {
			HashSet<String> labels = new HashSet<String>();
			for(Object headNode : ret.get("left")) {
				Iterator<Label> headLabels = ((Node)headNode).getLabels().iterator();
				while(headLabels.hasNext()) labels.add(headLabels.next().name());				
			}
			
			p.addObservation("domain", stringList(labels));
						
			labels = new HashSet<String>();
			for(Object tailNode : ret.get("right")) {
				Iterator<Label> tailLabels = ((Node)tailNode).getLabels().iterator();
				while(tailLabels.hasNext()) labels.add(tailLabels.next().name());				
			}
			
			p.addObservation("range", stringList(labels)); 			
		} // End try
			
		return p;
	}
}
