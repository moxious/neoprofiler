package org.mitre.neoprofiler.profiler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		
		Map<String,List<Object>> indexes = runQueryComplexResult(parent, "call db.indexes();", "description", "state", "type");
		Map<String,List<Object>> constraints = runQueryComplexResult(parent, "call db.constraints();", "description");

		for(Object index : indexes.get("description")) {
			p.addConstraint(new NeoConstraint("" + index, true));
		}

		for(Object constraint : constraints.get("description")) {
			p.addConstraint(new NeoConstraint(""+constraint));
		}

		return p;
	}

	public String describe() {
		return "SchemaProfiler";
	}
}
