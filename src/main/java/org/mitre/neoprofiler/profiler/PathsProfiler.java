package org.mitre.neoprofiler.profiler;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;

public class PathsProfiler extends QueryRunner implements Profiler {
	public NeoProfile run(NeoProfiler parent) {
		PathsProfile p = new PathsProfile();
		return p;
	}

	public class PathsProfile extends NeoProfile {
		public PathsProfile() {
			name = "PathsProfile";
			description = "Profile of various paths of interest through this graph.";
		}
	}	
} // End PathsProfiler
