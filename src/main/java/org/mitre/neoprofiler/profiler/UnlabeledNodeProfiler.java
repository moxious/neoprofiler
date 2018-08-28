package org.mitre.neoprofiler.profiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.LabelProfile;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.NeoProperty;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.Value;


public class UnlabeledNodeProfiler extends LabelProfiler {
	public static final String PARAMETER_LABEL = "(Unlabeled)";
	
	public UnlabeledNodeProfiler() {
		super(PARAMETER_LABEL);
	}

	public NeoProfile run(NeoProfiler parent) {
		LabelProfile p = new LabelProfile(label);

		Object result = runQuerySingleResult(parent, "match (n) where labels(n)=[] return count(n) as c", "c");
		p.addObservation(NeoProfile.OB_COUNT, "" + result);

		int sampleSize = (Integer) p.getParameter("sampleSize");

		List<Object> nodeSamples = runQueryMultipleResult(parent, "match (n) where labels(n)=[] return n as instance limit " + sampleSize,
				"instance");

		try (Transaction tx = parent.beginTx()) {
			HashSet<NeoProperty> props = new HashSet<NeoProperty>();
			HashMap<String, Integer> seen = new HashMap<String, Integer>();

			for (Object ns : nodeSamples) {
				for (NeoProperty prop : getSampleProperties(parent, ((Value) ns).asNode())) {
					String key = prop.toString();

					// Increment counter.
					if (!seen.containsKey(key)) {
						seen.put(key, 1);
					} else {
						seen.put(key, seen.get(key) + 1);
						continue;
					}

					props.add(prop);
				}
			}

			// If a property was seen in some (but not all) samples, then it's optional.
			for (NeoProperty prop : props) {
				int count = seen.get(prop.toString());
				if (count < sampleSize) prop.setOptional(true);
				else prop.setOptional(false);
			}

			p.addObservation(NeoProfile.OB_SAMPLE_PROPERTIES, props);
		} // End try

		List<Object> outbound = runQueryMultipleResult(parent,
				"match (n)-[r]->(m) where labels(n)=[] and n <> m return distinct(type(r)) as outbound", "outbound");

		if (outbound.isEmpty()) outbound.add(NeoProfile.OB_VALUE_NA);
		p.addObservation(LabelProfile.OB_OUTBOUND_RELATIONSHIP_TYPES, outbound);

		List<Object> inbound = runQueryMultipleResult(parent,
				"match (n)<-[r]-(m) where labels(n)=[] and n <> m return distinct(type(r)) as inbound", "inbound");

		if (inbound.isEmpty()) inbound.add(NeoProfile.OB_VALUE_NA);
		p.addObservation(LabelProfile.OB_INBOUND_RELATIONSHIP_TYPES, inbound);

		// TODO Auto-generated method stub
		return p;
	}

	public String describe() {
		return "UnlabeledNodeProfiler";
	}
}
