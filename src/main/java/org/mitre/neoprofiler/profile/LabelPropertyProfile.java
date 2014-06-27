package org.mitre.neoprofiler.profile;

public class LabelPropertyProfile extends ParameterizedNeoProfile {
	public String label;
	public String property;
	
	public static final String OB_CARDINALITY = "Cardinality";
	
	public LabelPropertyProfile(String label, String property) { 
		this.name = "Property '" + property + "' on '" + label + "' nodes";
		this.description = "Profile of the values of properties named '" + property + "' on nodes labeled '" + label + "'";
		this.label = label;
		this.property = property;
	}
}
