package org.mitre.neoprofiler.profile;

public class RelationshipTypeProfile extends ParameterizedNeoProfile {
	public static final String OB_DOMAIN = "domain";
	public static final String OB_RANGE = "range";
	
	public RelationshipTypeProfile(String type) { 
		name="Relationships Typed '" + type + "'"; 
		description="Profile of relationships of type '" + type + "'";
		setParameter("type", type);			
		setParameter("sampleSize", new Integer(100));
	}
}