package org.mitre.neoprofiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profiler.NodesProfiler;
import org.mitre.neoprofiler.profiler.Profiler;
import org.mitre.neoprofiler.profiler.RelationshipsProfiler;
import org.mitre.neoprofiler.profiler.SchemaProfiler;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class NeoProfiler {
	GraphDatabaseService svc = null;
	List<Profiler> schedule = new ArrayList<Profiler>();
	
	public NeoProfiler(GraphDatabaseService svc) { 
		this.svc = svc;
	}
	
	public GraphDatabaseService getDB() { return svc; } 
	
	public void schedule(Profiler p) { 
		schedule.add(p);
	}
	
	public Collection<NeoProfile> run() {
		schedule(new SchemaProfiler());
		schedule(new NodesProfiler());
		schedule(new RelationshipsProfiler());
		
		ArrayList<NeoProfile> results = new ArrayList<NeoProfile>();
		
		int x=0;
		
		while(x < schedule.size()) {
			Profiler profiler = schedule.get(x);
			System.out.println("Running " + profiler.getClass().getName());
			results.add(profiler.run(this));
			x++;
		}
		
		return results;
	} // End run
	
	public static void main(String [] args) throws Exception {
		String dbPath = "/home/x/neo20_mitre_db";
		
		if(args.length < 1) {
			System.out.println("Usage: NeoProfiler <path_to_db>");
			// System.exit(0);
		} else dbPath = args[0];
		
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		NeoProfiler profiler = new NeoProfiler(graphDb);
		
		Collection<NeoProfile> profiles = profiler.run();
		for(NeoProfile p: profiles) { 
			System.out.println(p + "\n");
		}
	}
} // End NeoProfiler
