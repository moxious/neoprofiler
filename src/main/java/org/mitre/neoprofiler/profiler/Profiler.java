package org.mitre.neoprofiler.profiler;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;

public interface Profiler {
	public NeoProfile run(NeoProfiler parent);
}
