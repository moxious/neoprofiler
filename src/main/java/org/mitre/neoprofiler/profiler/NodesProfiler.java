package org.mitre.neoprofiler.profiler;

import java.util.List;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.NodesProfile;

/**
 * Profiling queries to be run on all nodes.  This has the effect of spawning additional profilers as needed.
 * @author moxious
 */
public class NodesProfiler extends QueryRunner implements Profiler {
	public NeoProfile run(NeoProfiler parent) {
		NodesProfile p = new NodesProfile();

		List<Object> labels = runQueryMultipleResult(parent, "match n return distinct(labels(n)) as labels", "labels");
		
		for(Object l : labels) { 
			// Schedule a new profiler to look into the particular label we just discovered.
			parent.schedule(new LabelProfiler(""+l));
		}
		
		p.addObservation("Available Node Labels", stringList(labels));		
		p.addObservation("Total Nodes", runQuerySingleResult(parent, "match n return count(n) as c", "c"));
		
		return p;
	} // End run
} // End NodesProfiler
