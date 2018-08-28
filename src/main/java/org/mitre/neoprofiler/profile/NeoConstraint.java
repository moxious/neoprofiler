package org.mitre.neoprofiler.profile;

public class NeoConstraint {
	protected String description;
	protected boolean index;

	public NeoConstraint(String description) {
		this(description, false);
	}

	public NeoConstraint(String description, boolean index) {
		this.description = description;
		this.index = index;
	}

	public String toString() {
		return "NeoConstraint constraint=" + description;
	}

	public boolean isIndex() { return index == true; }
	public String getDescription() { return description; }
} // End NeoConstraint
