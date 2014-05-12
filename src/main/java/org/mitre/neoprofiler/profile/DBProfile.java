package org.mitre.neoprofiler.profile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBProfile extends NeoProfile {
	protected List<NeoProfile> profiles = new ArrayList<NeoProfile>();
		
	public DBProfile(String storageLoc) {		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		name = "Profile of " + storageLoc + " generated " + dateFormat.format(date);
		description = storageLoc;
	}
	
	public List<NeoProfile> getProfiles() { return profiles; } 
	
	public List<LabelProfile> getLabels() { 
		ArrayList<LabelProfile> arr = new ArrayList<LabelProfile>();
		
		for(NeoProfile p : getProfiles())
			if(p instanceof LabelProfile)
				arr.add((LabelProfile)p);
		
		return arr;
	}
	
	public List<RelationshipTypeProfile> getRelationshipTypes() { 
		ArrayList<RelationshipTypeProfile> arr = new ArrayList<RelationshipTypeProfile>();
		
		for(NeoProfile p : getProfiles())
			if(p instanceof RelationshipTypeProfile)
				arr.add((RelationshipTypeProfile)p);
		
		return arr;
	}
	
	public RelationshipTypeProfile getRelationshipTypeProfile(String type) { 
		for(NeoProfile p : getProfiles()) { 
			if(!(p instanceof RelationshipTypeProfile)) continue;
			
			if(type.equals(((RelationshipTypeProfile)p).getParameter("type"))) 
				return ((RelationshipTypeProfile)p);
		}
		
		return null;
	}
	
	public LabelProfile getLabelProfile(String label) { 
		for(NeoProfile p : getProfiles()) { 
			if(!(p instanceof LabelProfile)) continue;
			
			if(label.equals(((LabelProfile)p).getParameter("label"))) 
				return ((LabelProfile)p);
		}
		
		return null;
	}
		
	public void addProfile(NeoProfile profile) { 
		profiles.add(profile);
	}
	
	public String toString() { 
		StringBuffer b = new StringBuffer("");
		
		b.append("DBProfile " + name + "\n");
		b.append("DBProfile " + description + "\n\n");
		
		for(NeoProfile profile : profiles) { 
			b.append(profile.toString() + "\n"); 
		}
		
		return b.toString();
	}
} // End DBProfile
