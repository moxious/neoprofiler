package org.mitre.neoprofiler.profiler;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

		List<Object> labels = runQueryMultipleResult(parent, "match (n) return distinct(labels(n)) as labels", "labels");

		for(Object l : labels) {
			String labelTxt;

			// Schedule a new profiler to look into the particular label we just discovered.
			if (l instanceof List) {
				List<String> labelNames = (List<String>) l;
				labelTxt = StringUtils.join(labelNames, ":");
			} else {
				labelTxt = ""+l;
			}


			// If the database has no labels, then querying for them returns the empty set [].
			// Don't try to schedule inspection of [] as a label, because that will bomb.
			if(labelTxt != null && !"".equals(labelTxt) && !"[]".equals(labelTxt))
				parent.schedule(new LabelProfiler(labelTxt));
		}

		p.addObservation("Node Labels", labels);
		p.addObservation(NeoProfile.OB_COUNT, runQuerySingleResult(parent, "match (n) return count(n) as c", "c"));

		return p;
	} // End run

	public String describe() {
		return "NodesProfiler";
	}
} // End NodesProfiler
