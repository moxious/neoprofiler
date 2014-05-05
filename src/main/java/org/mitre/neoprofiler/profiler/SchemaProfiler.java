package org.mitre.neoprofiler.profiler;

import java.util.Iterator;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintType;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

public class SchemaProfiler extends QueryRunner implements Profiler {
	public class SchemaProfile extends NeoProfile {
		public SchemaProfile() {
			name="SchemaProfile";
			description="Information about Neo4J's database schema";
		}
	}
	
	public NeoProfile run(NeoProfiler parent) {
		SchemaProfile p = new SchemaProfile();
		
		try(Transaction tx = parent.getDB().beginTx()) {
			Schema schema = parent.getDB().schema();
		
			Iterator<ConstraintDefinition> constraints = schema.getConstraints().iterator();
			
			int x=1;
			while(constraints.hasNext()) {			
				ConstraintDefinition c = constraints.next();
				ConstraintType ct = c.getConstraintType();
			
				p.addObservation("Constraint " + x + " type", ""+ct);
				p.addObservation("Constraint " + x + " property keys", stringList(c.getPropertyKeys()));
				p.addObservation("Constraint " + x + " label", (c.getLabel() == null ? "N/A" : c.getLabel().name()));
				
				x++;
			}
			
			x=1;
			Iterator<IndexDefinition> idxs = schema.getIndexes().iterator();
			while(idxs.hasNext()) {
				IndexDefinition idx = idxs.next();
				
				p.addObservation("Index " + x + " is constraint", idx.isConstraintIndex());
				p.addObservation("Index " + x + " property keys", stringList(idx.getPropertyKeys()));
				p.addObservation("Index " + x + " label", (idx.getLabel() == null ? "N/A" : idx.getLabel().name()));
				
				x++;
			}
		}
		return p;
	}

}
