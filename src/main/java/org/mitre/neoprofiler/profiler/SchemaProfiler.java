package org.mitre.neoprofiler.profiler;

import java.util.Iterator;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoConstraint;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.SchemaProfile;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Record;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

/**
 * Profiler which pulls whatever information is possible out of the Schema object provided by Neo4J
 * @author moxious
 */
public class SchemaProfiler extends QueryRunner implements Profiler {	
	public NeoProfile run(NeoProfiler parent) {
		SchemaProfile p = new SchemaProfile();
		
		Session s = parent.getDriver().session();
		StatementResult result = s.run("CALL db.schema()");

		while(result.hasNext()) {
			Record r = result.next();
			// Blah fix later
		}
		/*
		try(Transaction tx = s.beginTransaction()) {
			Schema schema = parent.getDB().schema();
		
			Iterator<ConstraintDefinition> constraints = schema.getConstraints().iterator();
						
			while(constraints.hasNext()) {			
				ConstraintDefinition c = constraints.next();				
				p.addConstraint(new NeoConstraint(true, false, c.getPropertyKeys(), c.getLabel(), c.getConstraintType()));				
			}
						
			Iterator<IndexDefinition> idxs = schema.getIndexes().iterator();
			while(idxs.hasNext()) {
				IndexDefinition idx = idxs.next();
				p.addConstraint(new NeoConstraint(idx.isConstraintIndex(), true, idx.getPropertyKeys(), idx.getLabel(), null));				
			}
		}
		*/
		return p;
	}
}
