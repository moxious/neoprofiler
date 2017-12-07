package org.mitre.neoprofiler.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.schema.ConstraintType;

public class NeoConstraint {
	public boolean constraint = false;
	public boolean index = false;
	public List<String> propertyKeys = new ArrayList<String>();
	public ConstraintType type = ConstraintType.UNIQUENESS;
	public String label = "";
	
	public NeoConstraint(boolean constraint, boolean index, Iterable<String>propKeys, Label label, ConstraintType type) {
		this.constraint = constraint;
		this.index = index;
		this.type = type;

		Iterator<String> i = propKeys.iterator();
		while(i.hasNext()) {
			this.propertyKeys.add(i.next());
		}
		this.label = (label == null ? "N/A" : label.name());		
	}
	
	public String toString() {
		return "NeoConstraint constraint=" + constraint + 
				" index=" + index + 
				" propertyKeys=" + propertyKeys + 
				" type=" + type + 
				" label=" + label;
	}
} // End NeoConstraint
