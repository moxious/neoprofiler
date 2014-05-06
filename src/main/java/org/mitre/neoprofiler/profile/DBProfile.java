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
		
		name = "NeoProfile of " + storageLoc + " generated " + dateFormat.format(date);
		description = storageLoc;
	}
	
	public List<NeoProfile> getProfiles() { return profiles; } 
	
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
