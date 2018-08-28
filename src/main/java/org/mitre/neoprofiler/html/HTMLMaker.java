package org.mitre.neoprofiler.html;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.mitre.neoprofiler.markdown.MarkdownMaker;
import org.mitre.neoprofiler.profile.DBProfile;
import org.mitre.neoprofiler.profile.LabelProfile;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.RelationshipTypeProfile;

/**
 * Class that renders a DBProfile as HTML.
 * The general approach is to write a graph structure in JSON rendered via D3.js, and to populate the rest
 * of the page with regular markdown from the MarkdownGenerator, rendered to HTML by strapdown.js. 
 * @author moxious
 */
public class HTMLMaker {
	public HTMLMaker() { ; } 
	
	public String generateGraph(DBProfile profile) { 
		StringBuffer b = new StringBuffer("var links = [\n");
		
		for(NeoProfile p : profile.getProfiles()) {
			if(p instanceof LabelProfile) {				
				LabelProfile lp = (LabelProfile)p;
				String label = (String)lp.getParameter("label");
				
				List<Object> inbound = (List<Object>)lp.getObservations().get(LabelProfile.OB_INBOUND_RELATIONSHIP_TYPES);
				List<Object> outbound = (List<Object>)lp.getObservations().get(LabelProfile.OB_OUTBOUND_RELATIONSHIP_TYPES);
				
				for(Object o : outbound) { 
					if((""+outbound).contains(NeoProfile.OB_VALUE_NA)) continue;
					b.append("{source: '[" + label + "]', target: 'REL: " + o + "', head: 'node', tail: 'relationship', type: 'outbound'},\n");
				}
				
				for(Object o : inbound) { 
					if((""+inbound).contains(NeoProfile.OB_VALUE_NA)) continue;
					b.append("{source: 'REL: " + o + "', target: '[" + label + "]', head: 'relationship', tail: 'node', type: 'inbound'},\n");
				}
			} else if(p instanceof RelationshipTypeProfile) { 
				RelationshipTypeProfile rt = (RelationshipTypeProfile)p;
				String type = (String)rt.getParameter("type");
				
				Collection<String> domain = (Collection<String>)rt.getObservations().get(RelationshipTypeProfile.OB_DOMAIN);
				Collection<String> range = (Collection<String>)rt.getObservations().get(RelationshipTypeProfile.OB_RANGE);
				
				for(String d : domain) { 
					if(NeoProfile.OB_VALUE_NA.equals(""+d)) continue;
					b.append("{source: 'REL: " + type + "', target: '[" + d + "]', head: 'relationship', tail: 'node', type: 'domain'},\n");
				}
				
				for(String r : range) {
					if(NeoProfile.OB_VALUE_NA.equals(""+r)) continue;
					b.append("{source: '[" + r + "]', target: 'REL: " + type + "', head: 'node', tail: 'relationship', type: 'range'},\n");
				}
			}
		}
		
		b.append("];\n");
		return b.toString();
	}
	
	public void html(DBProfile profile, Writer writer) throws IOException {
		VelocityEngine ve = new VelocityEngine();
        
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		
		ve.init();
        
		Template t = null;
		
		if(ve.resourceExists("/template.html"))
			t = ve.getTemplate("/template.html");
		else {
			try { 
				t = ve.getTemplate("src/main/resources/template.html");				
			} catch(Exception exc) { 
				System.err.println("The application could not find a needed HTML template.");
				System.err.println("This is an unusual problem; please report it as an issue, and provide details of your configuration.");
				throw new RuntimeException("Could not find HTML template as resource.");
			}
		}
		
        VelocityContext context = new VelocityContext();

        StringWriter markdownContent = new StringWriter();
        
        // Write markdown content....
        new MarkdownMaker().markdown(profile, markdownContent);
        
        context.put("title", profile.getName());
        context.put("markdown", markdownContent.getBuffer().toString());
        context.put("links", generateGraph(profile)); 
        //context.put("d3graph", d3graph.toString());

        // Dump the contents of the template merged with the context.  That's it!
        t.merge(context, writer);
	}
}
