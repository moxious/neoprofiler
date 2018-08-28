package org.mitre.neoprofiler.profiler;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;

/**
 * Profiler for paths through the database; currently doesn't do much other than act as a placeholder.
 * @author moxious
 * TODO
 */
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

	public String describe() {
		return "PathsProfiler";
	}
} // End PathsProfiler
