package org.mitre.neoprofiler.profiler;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.LabelPropertyProfile;
import org.mitre.neoprofiler.profile.NeoProfile;


public class LabelPropertyProfiler extends QueryRunner implements Profiler {
	protected String label;
	protected String property;
	
	public LabelPropertyProfiler(String label, String property) { 
		this.label = label;
		this.property = property;
	}

	public NeoProfile run(NeoProfiler parent) {
		LabelPropertyProfile prof = new LabelPropertyProfile(label, property);
		
		// Determine how many unique observations 
		prof.addObservation(LabelPropertyProfile.OB_CARDINALITY, 
				runQuerySingleResult(parent, "match (n:`" + label + "`) return count(distinct(n.`" + property +"`)) as n", "n"));
		
		return prof;
	}

	public String describe() {
		return "LabelPropertyProfiler - (:" + label + " { " + property + " })";
	}
}
