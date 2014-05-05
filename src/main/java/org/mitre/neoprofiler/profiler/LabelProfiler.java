package org.mitre.neoprofiler.profiler;

import java.util.List;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class LabelProfiler extends QueryRunner implements Profiler {
	String label = null;
	
	class LabelProfile extends NeoProfile {
		public LabelProfile(String label) { 
			name="NodeProfile"; description="Profile of nodes labeled '" + label + "'";
		}
	}
	
	public LabelProfiler(String label) {
		this.label = label.replaceAll("\\[", "").replaceAll("\\]", "");
	}
	
	public NeoProfile run(NeoProfiler parent) {		
		NeoProfile p = new LabelProfile(label);
		
		Object result = runQuerySingleResult(parent, "match (n:" + label +") return count(n) as c", "c");
		p.addObservation("Total nodes", ""+result);
		
		List<Object>nodeSamples = runQueryMultipleResult(parent, "match (n:" + label + ") return n as instance limit 10", "instance");
		
		try ( Transaction tx = parent.getDB().beginTx() ) {
			for(Object ns : nodeSamples) { 				
				p.addObservation("Sample properties", stringList(getSampleProperties(parent, (Node)ns)));
			}
		} // End try
			
		List<Object>outbound = runQueryMultipleResult(parent, 
				"match (n:" + label + ")-[r]->m where n <> m return distinct(type(r)) as outbound", "outbound");
		p.addObservation("Outbound relationship types", stringList(outbound));
		
		List<Object>inbound = runQueryMultipleResult(parent, 
				"match (n:" + label + ")<-[r]-m where n <> m return distinct(type(r)) as outbound", "outbound");
		p.addObservation("Inbound relationship types", stringList(inbound)); 
		
		// TODO Auto-generated method stub
		return p;
	}
}
