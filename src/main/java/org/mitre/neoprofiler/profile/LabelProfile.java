package org.mitre.neoprofiler.profile;

public class LabelProfile extends ParameterizedNeoProfile {
	public static String OB_OUTBOUND_RELATIONSHIP_TYPES = "Outbound relationship types";
	public static String OB_INBOUND_RELATIONSHIP_TYPES = "Outbound relationship types";
	
	public LabelProfile(String label) { 
		name="Label '" + label + "'"; 
		description="Profile of nodes labeled '" + label + "'";
		setParameter("label", label);
		setParameter("sampleSize", new Integer(100));
	}
}