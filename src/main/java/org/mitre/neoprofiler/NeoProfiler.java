package org.mitre.neoprofiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mitre.neoprofiler.markdown.MarkdownMaker;
import org.mitre.neoprofiler.profile.DBProfile;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profiler.NodesProfiler;
import org.mitre.neoprofiler.profiler.Profiler;
import org.mitre.neoprofiler.profiler.RelationshipsProfiler;
import org.mitre.neoprofiler.profiler.SchemaProfiler;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NeoProfiler {
	protected GraphDatabaseService svc = null;
	protected String storageLoc = null;
	protected List<Profiler> schedule = new ArrayList<Profiler>();
	
	public enum Format { MARKDOWN, HTML, JSON };
	
	public NeoProfiler(String storageLoc, GraphDatabaseService svc) { 
		this.svc = svc;
		this.storageLoc = storageLoc;
	}
	
	public GraphDatabaseService getDB() { return svc; } 
	
	public void schedule(Profiler p) { 
		schedule.add(p);
	}
	
	public DBProfile run() {
		DBProfile p = new DBProfile(storageLoc);
		
		schedule(new SchemaProfiler());
		schedule(new NodesProfiler());
		schedule(new RelationshipsProfiler());
		
		int x=0;
		
		while(x < schedule.size()) {
			Profiler profiler = schedule.get(x);
			System.out.println("Running " + profiler.getClass().getSimpleName() + " (" + (x+1) + " of " + schedule.size() + ")");
			long t1 = System.currentTimeMillis();
			
			NeoProfile prof = null;
			
			try { 
				prof = profiler.run(this);
			} catch(Exception exc) { 
				System.err.println(profiler.getClass().getName() + " failed.");
				continue;
			}
			long t2 = System.currentTimeMillis();
			
			prof.addObservation("Run Time (ms)", (t2 - t1));
			
			if(prof != null) p.addProfile(prof);
			x++;
		}
		
		return p;
	} // End run
	
	public static Options makeCLIOptions() { 
		Options options = new Options();
		
		options.addOption(OptionBuilder.withArgName("db")
				          .hasArg()
				          .isRequired()
				          .withDescription("Path to directory where neo4j database is located")
				          .create("db"));
		
		options.addOption(OptionBuilder.withArgName("format")
				          .hasArg()
				          .isRequired()
				          .withDescription("Output format: valid values are json, markdown, or html")
				          .create("format"));

		options.addOption(OptionBuilder.withArgName("output")
				          .hasArg()
				          .isRequired(false)
				          .withDescription("Name of output file to write; program will print to console if not specified.")
				          .create("output"));
		
		return options;
	}
	
	public static void main(String [] args) throws Exception { 
		CommandLineParser parser = new GnuParser();
		
		try { 
			CommandLine line = parser.parse(makeCLIOptions(), args );			
			
			String path = line.getOptionValue("db");
			String format = line.getOptionValue("format");
			String output = line.getOptionValue("output");
			
			Writer destination = null;
			if(output == null) destination = new StringWriter();
			
			File f = new File(path);

			Format fmt = Format.HTML;	
			
			if(format == null) fmt = Format.HTML;			
			else if("html".equals(format)) fmt = Format.HTML;
			else if("json".equals(format)) fmt = Format.JSON;
			else if("markdown".equals(format)) fmt = Format.MARKDOWN;
			else {
				System.err.println("Invalid or unrecognized format '" + format + "': using default of html");
			}
			
			if(!f.exists()) {
				System.err.println("Specified database at " + path + ": path does not exist.");
				usage();
				System.exit(1);
			} else if(!f.isDirectory()) {
				System.out.println("Specified database at " + path + ": path does not refer to a directory.");
				usage();
				System.exit(1);				
			} else if(path.contains("http:")) {
				System.err.println("HTTP endpoints are not yet supported.");
				usage();
				System.exit(1);
			} 

			
			if(output != null) {
				try { destination = new FileWriter(output); }
				catch(IOException exc) { 
					System.err.println("Can not create output file at " + output + ": " + exc.getMessage());
					usage();
					System.exit(1);
				}
			} else { 
				// Instead of writing to a file, accumulate in this buffer then print out later.
				destination = new StringWriter();
			}
			
			// Run the profile.
			profile(path, fmt, destination);
			
			// Check for if final results are written to console, or out to a file.
			if(output == null) System.out.println(((StringWriter)destination).getBuffer());
			else destination.close();
			
			System.out.println("Done.");
			System.exit(0);
		} catch(ParseException exc) {
	        System.err.println(exc.getMessage());
	        usage();
	        System.exit(1);
		}
	}

	public static void usage() {
		HelpFormatter formatter = new HelpFormatter();			
		formatter.printHelp("NeoProfiler", makeCLIOptions());
	}
	
	public static void profile(String path, Format fmt, Writer output) throws IOException {
		GraphDatabaseService svc = new GraphDatabaseFactory().newEmbeddedDatabase(path);
		
		NeoProfiler profiler = new NeoProfiler(path, svc);
		
		DBProfile profile = profiler.run();		
		MarkdownMaker mm = new MarkdownMaker();
		
		System.out.println("Writing report...");
		switch(fmt) { 
		case JSON:
			Gson gson = new GsonBuilder().setPrettyPrinting().create();		
			output.write(gson.toJson(profile));
			break;
			
		case MARKDOWN:			
			mm.markdown(profile, output);
			break;
			
		case HTML:			
			mm.html(profile, output);
			break;
			
		default:
			throw new RuntimeException("Invalid format");
		}
	}
} // End NeoProfiler
