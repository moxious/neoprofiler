package org.mitre.neoprofiler.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProperty;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 * Abstract class that contains various query running utilities to make implementation of downstream profilers easier.
 * @author moxious
 */
public abstract class QueryRunner {
	protected static final Logger log = Logger.getLogger(QueryRunner.class.getName());
				
	public List<NeoProperty> getSampleProperties(NeoProfiler parent, PropertyContainer n) {
		List<NeoProperty> props = new ArrayList<NeoProperty>();
		
		try ( Transaction tx = parent.getDB().beginTx() ) {			 
			Iterator<String> propKeys = n.getPropertyKeys().iterator();
			
			while(propKeys.hasNext()) {
				String key = propKeys.next();
				Object val = n.getProperty(key);
				
				props.add(new NeoProperty(key, val.getClass().getSimpleName()));				
			}
			
		} // End try
		
		return props;
	}
	
	public Map<String,List<Object>> runQueryComplexResult(NeoProfiler parent, String query, String...columns) {
		HashMap<String,List<Object>> all = new HashMap<String,List<Object>>();
		
		ExecutionEngine engine = new ExecutionEngine(parent.getDB());
		List<Object> retvals = new ArrayList<Object>();
		
		try ( Transaction tx = parent.getDB().beginTx() ) {
			// log.info(query);
			ExecutionResult result = engine.execute(query);
			
			ResourceIterator<Map<String,Object>> rows = result.iterator();
			
			while(rows.hasNext()) {
				Map<String,Object> row = rows.next();
				
				for(String col : columns) { 
					if(!all.containsKey(col)) all.put(col, new ArrayList<Object>());
					all.get(col).add(row.get(col));
				}
			}
			
			rows.close();
		} // End try
		
		return all;
	}
	
	public Object runQuerySingleResult(NeoProfiler parent, String query, String columnReturn) {		
		ExecutionEngine engine = new ExecutionEngine(parent.getDB());
		
		try ( Transaction tx = parent.getDB().beginTx() ) {
			// log.info(query);
			ExecutionResult result = engine.execute(query);
			ResourceIterator<Object> vals = result.columnAs(columnReturn);
						
			if(vals.hasNext()) {
				Object retval = vals.next();
				vals.close();
				return retval;
			} 
			
			return null;
		}		
	}
	
	public List<Object> runQueryMultipleResult(NeoProfiler parent, String query, String columnReturn) {
		ExecutionEngine engine = new ExecutionEngine(parent.getDB());
		List<Object> retvals = new ArrayList<Object>();
		
		try ( Transaction tx = parent.getDB().beginTx() ) {
			// log.info(query);
			ExecutionResult result = engine.execute(query);
			ResourceIterator<Object> rit = result.columnAs(columnReturn);
					
			while(rit.hasNext()) {				
				retvals.add(rit.next());
			}					
		}
		
		return retvals;
	}

	/*
	public String stringList(Iterable<?> objs) { 
		StringBuffer b = new StringBuffer("");		
		Iterator<?> it = objs.iterator();		
		while(it.hasNext()) b.append(""+it.next() + " ");		
		return b.toString();		
	}
	
	public String stringList(List<Object> objs) { 
		StringBuffer b = new StringBuffer("");
		
		for(int x=0; x<objs.size(); x++) { 
			b.append(""+objs.get(x));
			if(x < (objs.size() - 1)) b.append(", ");
		}
		
		return b.toString();
	}
	*/
}
