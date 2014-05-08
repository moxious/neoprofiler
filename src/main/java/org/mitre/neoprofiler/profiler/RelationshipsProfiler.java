package org.mitre.neoprofiler.profiler;

import java.util.List;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.RelationshipsProfile;

public class RelationshipsProfiler extends QueryRunner implements Profiler {
	public NeoProfile run(NeoProfiler parent) {
		RelationshipsProfile p = new RelationshipsProfile();
		List<Object> relTypes = runQueryMultipleResult(parent, "match n-[r]->m return distinct(type(r)) as relTypes", "relTypes");
		p.addObservation("Available Relationship Types", relTypes);

		for(Object relType : relTypes) { 
			parent.schedule(new RelationshipTypeProfiler(""+relType));
		}
		
		p.addObservation("Total Relationships", runQuerySingleResult(parent, "match n-[r]->m return count(distinct(r)) as c", "c"));
		
		return p;
	}
} // End RelationshipsProfiler
