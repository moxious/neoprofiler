package org.mitre.neoprofiler.profile;

import java.util.ArrayList;
import java.util.List;

public class SchemaProfile extends NeoProfile {
	List<NeoConstraint> constraints;
	
	public SchemaProfile() {
		name="Schema";
		description="Information about Neo4J's database schema";
		
		constraints = new ArrayList<NeoConstraint>();
	}
	
	public void addConstraint(NeoConstraint constraint) { 
		constraints.add(constraint);
	}
	
	public List<NeoConstraint> getConstraints() { 
		return constraints;
	}
	
	public List<NeoConstraint> getNonIndexes() {
		List<NeoConstraint> idxs = new ArrayList<NeoConstraint>();
		
		for(NeoConstraint c : constraints) { 
			if(!c.index) idxs.add(c);
		}
		
		return idxs;
	}
	
	public List<NeoConstraint> getIndexes() {
		List<NeoConstraint> idxs = new ArrayList<NeoConstraint>();
		
		for(NeoConstraint c : constraints) { 
			if(c.index) idxs.add(c);
		}
		
		return idxs;
	}
} // End SchemaProfile
