package org.mitre.neoprofiler.profile;

import java.util.ArrayList;
import java.util.List;

public class SchemaProfile extends NeoProfile {
	List<NeoConstraint> constraints;
	
	public SchemaProfile() {
		name="SchemaProfile";
		description="Information about Neo4J's database schema";
		
		constraints = new ArrayList<NeoConstraint>();
	}
	
	public void addConstraint(NeoConstraint constraint) { 
		constraints.add(constraint);
	}
	
	public List<NeoConstraint> getConstraints() { 
		return constraints;
	}
} // End SchemaProfile
