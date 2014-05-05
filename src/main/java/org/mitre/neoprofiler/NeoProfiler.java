package org.mitre.neoprofiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
			System.out.println("Running " + profiler.getClass().getName());
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
	
	public static void main(String [] args) throws Exception {
		String dbPath = "/home/x/neo20_mitre_db";
		
		if(args.length < 1) {
			System.out.println("Usage: NeoProfiler <path_to_db>");
			System.exit(1);
		} else dbPath = args[0];
		
		GraphDatabaseService svc = null;
		
		File f = new File(args[0]);
		if(f.exists()) {
			if(!f.isDirectory()) {
				System.out.println("Usage: NeoProfiler <path_to_db>");
				System.out.println("The path argument should refer to a directory containing the database files.");
				System.exit(1);
			}			
			
			svc = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		} else if(args[0].contains("http")) {
			// svc = new RestGraphDatabase(args[0]);
			System.out.println("HTTP endpoints are not yet supported.");
			System.exit(1);
		} else {
			System.err.println("Invalid database provided, or path does not exist.");
			System.out.println("Usage: NeoProfiler <path_to_db>");
			System.exit(1);			
		}
		
		NeoProfiler profiler = new NeoProfiler(dbPath, svc);
		
		DBProfile profile = profiler.run();		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();		
		System.out.println(gson.toJson(profile));
	}
} // End NeoProfiler
