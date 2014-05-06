package org.mitre.neoprofiler.markdown;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mitre.neoprofiler.profile.DBProfile;
import org.mitre.neoprofiler.profile.NeoConstraint;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.ParameterizedNeoProfile;
import org.mitre.neoprofiler.profile.SchemaProfile;

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
				b.append("    * " + o + "\n");
			}
			
			return b.toString();
		}
		
		return ""+ob;
	} // End observation
	
	public String constraints(String title, List<NeoConstraint> constraints) {
		StringBuffer b = new StringBuffer("");
		b.append(h3(title));
		
		if(constraints.isEmpty()) b.append("**None found**");
		else {
			for(NeoConstraint con : constraints) {
				String type = (con.type == null ? "" : "Type " + con.type);
				String lab = ((!"".equals(con.label) && con.label != null) ? "On nodes labeled " + con.label : ""); 
				
				b.append("* " + type + " " + lab);
				
				if(con.propertyKeys.isEmpty()) b.append("\n");
				else {
					b.append(".  Property keys:\n");
					for(String pk : con.propertyKeys) { 
						b.append("    * " + pk + "\n");
					}
				}				
			}
		}		
		
		return b.toString();
	}
	
	public String parameters(Map<String,Object>parameters) {
		if(parameters.isEmpty()) return "";
		
		StringBuffer b = new StringBuffer("");
		
		b.append(h3("Parameters"));
				
		ArrayList<String>keys = new ArrayList<String>(parameters.keySet());
		Collections.sort(keys);
		
		for(String key : keys) { 
			b.append("* " + key + ":  " + observation(parameters.get(key)) + "\n");
		}
		
		b.append("\n");
		
		return b.toString();
	}
		
	public void markdownComponentProfile(NeoProfile profile, Writer writer) throws IOException {
		writer.write(h2(profile.getName()) + profile.getDescription() + "\n");
		
		//if(profile instanceof ParameterizedNeoProfile) { 
		//	writer.write(parameters(((ParameterizedNeoProfile)profile).getParameters()));
		//}
		
		if(profile instanceof SchemaProfile) {
			SchemaProfile sp = (SchemaProfile)profile;
			writer.write(constraints("Indexes", sp.getIndexes()));
			writer.write(constraints("Non-Index", sp.getNonIndexes()));
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
	
	public void html(DBProfile profile, Writer writer) throws IOException {
		// To understand what's going on here, see http://strapdownjs.com/
		writer.write("<!DOCTYPE html>\n<html><title>" + profile.getName() + "</title>\n\n");
		writer.write("<xmp theme='spacelab' style='display:none;'>\n");
		
		markdown(profile, writer);
		
		writer.write("\n</xmp>\n");
		writer.write("<script src='http://strapdownjs.com/v/0.2/strapdown.js'></script>\n");		
		writer.write("<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js'></script>");
		writer.write("<script src='https://raw.github.com/jgallen23/toc/master/dist/jquery.toc.min.js'></script>");
		writer.write("<script src='https://github.com/zipizap/strapdown_template/raw/master/js/init_TOC.js'></script>");
		writer.write("</html>");
	} // End html
}
