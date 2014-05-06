package org.mitre.neoprofiler.markdown;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.mitre.neoprofiler.profile.DBProfile;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.ParameterizedNeoProfile;

/**
 * A class to write profiles as Markdown.
 * @author moxious
 */
public class MarkdownMaker {
	public MarkdownMaker() {
		
	}
	
	public String link(String text, String url) { return "[" + text + "](" + url + ")"; } 
	public String h1(String content) { return "\n# " + content + "\n"; }
	public String h2(String content) { return "\n## " + content + "\n"; }
	public String h3(String content) { return "\n### " + content + "\n"; }
	public String h4(String content) { return "\n#### " + content + "\n"; }
	
	protected String observations(Map<String,Object>observations) {
		if(observations.isEmpty()) return "";
		
		StringBuffer b = new StringBuffer("");
		
		b.append(h3("Observations"));
				
		ArrayList<String>keys = new ArrayList<String>(observations.keySet());
		Collections.sort(keys);
		
		for(String key : keys) { 
			b.append("* " + key + ": " + observation(observations.get(key)));
			
			if(b.charAt(b.length() - 1) != '\n') b.append("\n");
		}
		
		return b.toString();
	}

	protected String observation(Object ob) {
		if(ob == null) return "N/A";
		if(ob instanceof String || ob instanceof Number) return ""+ob;
		if(ob instanceof Collection) {
			StringBuffer b = new StringBuffer("\n");
			
			for(Object o : ((Collection)ob)) {
				b.append("  ** " + o + "\n");
			}
			
			return b.toString();
		}
		
		return ""+ob;
	} // End observation
	
	public String parameters(Map<String,Object>parameters) {
		if(parameters.isEmpty()) return "";
		
		StringBuffer b = new StringBuffer("");
		
		b.append("|Parameter    |  Value       |\n");
		b.append("|-------------|-------------:|\n");
		
		ArrayList<String>keys = new ArrayList<String>(parameters.keySet());
		Collections.sort(keys);
		
		for(String key : keys) { 
			b.append("| " + key + " | " + parameters.get(key) + " |\n");
		}
		
		return b.toString();
	}
		
	public void markdownComponentProfile(NeoProfile profile, Writer writer) throws IOException {
		writer.write(h2(profile.getName()) + profile.getDescription() + "\n");
		
		if(profile instanceof ParameterizedNeoProfile) { 
			writer.write(parameters(((ParameterizedNeoProfile)profile).getParameters()));
		}
		
		writer.write(observations(profile.getObservations()));
		writer.write("\n");
	}
	
	public void markdown(DBProfile profile, Writer writer) throws IOException {
		writer.write(h1(profile.getName()) + profile.getDescription() + "\n");
		
		writer.write(observations(profile.getObservations()));
		
		for(NeoProfile p : profile.getProfiles()) {
			markdownComponentProfile(p, writer); 
		}		
	}
}
