package org.mitre.neoprofiler.profile;

/**
 * Small model container class for a property on a node, along with a type inferred for that property based on sample data.
 * @author moxious
 */
public class NeoProperty {
	protected String name;
	protected String type;
	protected boolean optional = true;
	
	public NeoProperty(String name, String type) {
		if(name == null) name = "";
		if(type == null) type = "";
		
		this.name = name;
		this.type = type;
	}
	
	public void setOptional(boolean optional) { this.optional = optional; } 
	public boolean getOptional() { return optional; } 
	public String getName() { return name; } 
	public String getType() { return type; } 
	
	public boolean equals(Object other) { 
		if(!(other instanceof NeoProperty)) return false;
		
		NeoProperty o = (NeoProperty)other;
		
		if(!name.equals(o.getName())) return false;
		if(!type.equals(o.getType())) return false;
		
		return true;
	}
	
	public String toString() { 
		return name + " (" + type + ") " + (optional ? "optional" : "required");
	}
} // End NeoProperty
